package org.example;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class LinearCode {

    private static final Logger LOGGER = LoggerFactory.getLogger(LinearCode.class);
    private final int rows;
    private final int columns;
    private final int zNum;
    private final List<Integer> columnsParityNumber = new ArrayList<>();
    private final List<Integer> rowsParityNumber = new ArrayList<>();
    private DoubleMatrix2D generatorMatrix;
    private DoubleMatrix2D originalGeneratorMatrix;
    private final List<Integer> allAlphaList;
    private final DoubleMatrix2D pivotTableMatrix;
    private final DoubleMatrix2D parityMatrix;
    private int rowCounter = 0;
    private int columnCounter = 0;
    private DoubleMatrix1D parityRowMatrix = null;
    private final int[] rowsNonParity;
    private final int[] columnsParity;

    public LinearCode(int rows, int columns, int zNum, List<Integer> allAlphaList) {
        this.rows = rows;
        this.columns = columns;
        this.zNum = zNum;
        this.allAlphaList = allAlphaList;
        this.pivotTableMatrix = new DenseDoubleMatrix2D(columns, columns);
        this.parityMatrix = new DenseDoubleMatrix2D(columns - rows, columns);
        this.rowsNonParity = new int[rows - 1];
        this.columnsParity = new int[columns];
    }

    public LinearCode(int rows, int columns, int zNum, List<Integer> allAlphaList, DoubleMatrix2D generatorMatrix) {
        this.rows = rows;
        this.columns = columns;
        this.zNum = zNum;
        this.allAlphaList = allAlphaList;
        this.pivotTableMatrix = new DenseDoubleMatrix2D(columns, columns);
        this.parityMatrix = new DenseDoubleMatrix2D(columns - rows, columns);
        this.rowsNonParity = new int[rows - 1];
        this.columnsParity = new int[columns];
        this.generatorMatrix = generatorMatrix;
        this.originalGeneratorMatrix = generatorMatrix;
    }

    public void setGeneratorMatrix() {
        Scanner scan = new Scanner(System.in);
        generatorMatrix = new DenseDoubleMatrix2D(rows,columns);

        for (int i = 0; i < rows; i++) {
            LOGGER.info("Add meg a {}. sort (szóközzel elválasztva): ", i + 1);
            String row = scan.nextLine();

            int[] rowMatrix = Arrays.stream(row.split("\\s+"))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            for (int j = 0; j < columns; j++) {
                generatorMatrix.setQuick(i, j, rowMatrix[j]);
            }
        }

        originalGeneratorMatrix = generatorMatrix.copy();

        scan.close();
    }

    public void setPivotTable() {
        setColumnsParity();

        for (int h = 0; h < rows; h++) {
            LOGGER.info("Generator matrix: {}", generatorMatrix);

            getParityRowMatrix1D(containsGeneratorMatrixValue(generatorMatrix));
            setRowNonParity(getRowCounter());
            
            DoubleMatrix2D nonParityMatrix = generatorMatrix.viewSelection(rowsNonParity,columnsParity);

            if (parityRowMatrix != null) {
                setMatrixRow(parityRowMatrix, nonParityMatrix, getColumnCounter(), getRowCounter());
            }
        }
    }

    private void getParityRowMatrix1D(boolean isContainsValue) {
        boolean isParityElem = false;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (isParityElem) {
                    break;
                }

                if ((isContainsValue ? generatorMatrix.get(i, j) == 1 : (generatorMatrix.get(i, j) != 1 && generatorMatrix.get(i, j) != 0)) && !columnsParityNumber.contains(j) && !rowsParityNumber.contains(i)) {
                    setParityRowMatrix(generatorMatrix.viewRow(i));
                    setRowCounter(i);
                    setColumnCounter(j);
                    rowsParityNumber.add(i);
                    columnsParityNumber.add(j);
                    isParityElem = true;
                }
            }

        }
    }

    private void setMatrixRow(DoubleMatrix1D parityRowMatrix, DoubleMatrix2D nonParityMatrix, int columnCounter,int rowCounter) {
        int rowCount = 0;
        int modNum;
        int parityValue = (int) parityRowMatrix.get(columnCounter);

        for (int i = 0; i < rows; i++) {
            if(i == rowCounter) {
                continue;
            }

            DoubleMatrix1D parityFirsRowMatrix = nonParityMatrix.viewRow(rowCount);
            int rowIndexValue = (int) nonParityMatrix.get(rowCount,columnCounter);

            if(rowIndexValue == 0) {
                modNum = 0;
            } else if (parityRowMatrix.get(columnCounter) == 1) {
                modNum = ( rowIndexValue / parityValue) * -1;
            } else {
                modNum = alphaRowValue(allAlphaList, parityValue, rowIndexValue) * -1;
                LOGGER.info("Row modNum: {}", modNum);
            }

            while (modNum < 0) {
                modNum += zNum;
            }

            for (int j = 0; j < columns; j++) {
                int value = (int) (((parityRowMatrix.get(j) * modNum) + parityFirsRowMatrix.get(j)) % zNum);
                generatorMatrix.set(i, j, value);
            }

            rowCount++;
        }

        if (parityValue != 1) {
            setMatrixRowWithAlpha(parityRowMatrix, rowCounter, parityValue);
        }
    }

    private void setMatrixRowWithAlpha(DoubleMatrix1D parityRowMatrix, int rowCounter, int parityValue) {
        for (int j = 0; j < columns; j++) {
            int value;

            LOGGER.info("Parity Row Matrix: {}", parityRowMatrix);

            if((parityRowMatrix.get(j) / parityValue) == 0) {
                value = 0;
            } else if(parityRowMatrix.get(j) / parityValue == 1) {
                value = 1;
            } else {
                value  = alphaRowValue(allAlphaList, parityValue, (int) parityRowMatrix.get(j));
                LOGGER.info("Alpha row value: {}", value);
            }

            generatorMatrix.set(rowCounter, j, value);
        }
    }

    private boolean containsGeneratorMatrixValue(DoubleMatrix2D matrix) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (matrix.getQuick(i, j) == 1 && !columnsParityNumber.contains(j) && !rowsParityNumber.contains(i)) {
                    return true;
                }
            }
        }

        return false;
    }

    private int alphaRowValue(List<Integer> allAlpha, int parityItem, int nonParityItem) {
        int pIndex = allAlpha.indexOf(parityItem);
        int npIndex = allAlpha.indexOf(nonParityItem);

        if (npIndex < pIndex) {
            int k = 2;
            int result = 0;

            while (result != 1) {
                result = (parityItem * k) % zNum;


                if(result != 1) {
                    k++;
                }
            }

            return (nonParityItem * k) % zNum;
        }

        return allAlpha.get(npIndex - pIndex - 1);
    }

    public void setPivotTableFull() {
        LOGGER.info("Generator matrix: {}", generatorMatrix);

        int[] sortList = new int[columns];
        boolean isAddedJ = false;

        pivotTableMatrix.viewPart(0, 0, rows, columns).assign(generatorMatrix);

        for (int i = rows; i < columns; i++) {
            for (int j = 0; j < columns; j++) {
                if(!columnsParityNumber.contains(j) && !isAddedJ) {
                    int value = zNum - 1;
                    pivotTableMatrix.set(i, j, value);
                    isAddedJ = true;
                    columnsParityNumber.add(j);
                } else {
                    pivotTableMatrix.set(i, j, 0);
                }
            }
            isAddedJ = false;
        }

        for (int i = 0; i < columns; i++) {
            if (rows > i) {
                sortList[rowsParityNumber.get(i)] = columnsParityNumber.get(i);
            } else {
                sortList[i] = columnsParityNumber.get(i);
            }
        }
        
        LOGGER.info("Pivot matrix: {}", pivotTableMatrix);

        sortPivotTable(sortList);
    }

    private void sortPivotTable(int[] sortList) {
        DoubleMatrix2D sortedMatrix = new DenseDoubleMatrix2D(columns, columns);

        for (int i = 0; i < columns; i++) {
            int index = sortList[i];
            sortedMatrix.viewRow(index).assign(pivotTableMatrix.viewRow(i));
        }

        pivotTableMatrix.assign(sortedMatrix);

        LOGGER.info("Sorted pivot matrix:  {}", pivotTableMatrix);
    }

    public void setParityMatrix() {
        int row = 0;

        for (int i = rows; i < columns; i++) {
            int index = columnsParityNumber.get(i);
            parityMatrix.viewRow(row).assign(pivotTableMatrix.viewDice().viewRow(index));
            row++;
        }
    }

    public void getMultiplicationOfGeneratorMatrixByParityMatrix() {
        LOGGER.info("Parity matrix: {}", parityMatrix);

        Algebra algebra = new Algebra();
        DoubleMatrix2D matrix2D = algebra.mult(originalGeneratorMatrix, parityMatrix.viewDice());

        for (int i = 0; i < matrix2D.rows(); i++) {
            for (int j = 0; j < matrix2D.columns(); j++) {
                int value = (int) (matrix2D.get(i, j) % zNum);
                matrix2D.set(i, j, value);
            }
        }

        LOGGER.info("(getMultiplicationOfGeneratorMatrixByParityMatrix): {}", matrix2D);
    }

    private void setRowNonParity(int rowCounter) {
        int k = 0;

        for(int i = 0; i < rows; i++) {
            if(i == rowCounter) {
                continue;
            }
            rowsNonParity[k++] = i;
        }
    }

    private void setColumnsParity() {
        int k = 0;

        for(int i = 0; i < columns; i++) {
            columnsParity[k++] = i;
        }
    }

    public void setRowCounter(int rowCounter) {
        this.rowCounter = rowCounter;
    }

    public void setColumnCounter(int columnCounter) {
        this.columnCounter = columnCounter;
    }

    public int getRowCounter() {
        return rowCounter;
    }

    public int getColumnCounter() {
        return columnCounter;
    }

    public void setParityRowMatrix(DoubleMatrix1D parityRowMatrix) {
        this.parityRowMatrix = parityRowMatrix;
    }

    public DoubleMatrix2D getParityMatrix() {
        return parityMatrix;
    }
}
