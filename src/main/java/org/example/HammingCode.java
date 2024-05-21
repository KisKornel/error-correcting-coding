package org.example;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HammingCode {
    private static final Logger LOGGER = LoggerFactory.getLogger(HammingCode.class);
    private final int qNum;
    private final int rNum;
    private int nValue;
    private int kValue;
    private DoubleMatrix2D parityMatrix2D;
    private final List<Integer> allALphaList;

    public HammingCode(int qNum, int rNum, List<Integer> allALphaList) {
        this.qNum = qNum;
        this.rNum = rNum;
        this.allALphaList = allALphaList;
    }

    public void getHammingCode() {
        Algebra algebra = new Algebra();

        setNAndKValue();
        LOGGER.info("K value: {} and N value: {}, ", getKValue(), getNValue());

        List<Integer[]> qVectorList = vectorQList();
        LOGGER.info("Q vector list: {}", getQVectorList(qVectorList));

        getParityMatrix(getQVectorList(qVectorList));

        int[] msgWord = setMessageWord();
        LOGGER.info("Message word: {}", msgWord);
        LOGGER.info("Parity matrix: {}", parityMatrix2D);

        LinearCode linearCode = new LinearCode(2, getNValue(), qNum, allALphaList, parityMatrix2D);
        linearCode.setPivotTable();
        linearCode.setPivotTableFull();
        linearCode.setParityMatrix();
        linearCode.getMultiplicationOfGeneratorMatrixByParityMatrix();

        DoubleMatrix2D codeMsg = setCodeWord(msgWord, linearCode.getParityMatrix(), algebra);
        LOGGER.info("Code word: {}", codeMsg);

        DoubleMatrix2D modifyCodeMsg = modifyCodeWord(codeMsg);
        LOGGER.info("Modify code word: {}", modifyCodeMsg);

        DoubleMatrix2D syndromeMatrix = setSyndromeMatrix(modifyCodeMsg, algebra);
        LOGGER.info("Syndrome matrix: {}", syndromeMatrix);

        if (syndromeMatrix.zSum() != 0) {
            LOGGER.info("Hiba történt!");
        }

        int syndromeErrorValue = syndromeErrorValue(syndromeMatrix);
        LOGGER.info("Hiba értéke: {}", syndromeErrorValue);

        int syndromeErrorValueLocation = syndromeErrorValueLocation(syndromeErrorValue, syndromeMatrix);
        LOGGER.info("Hiba helye: {}", syndromeErrorValueLocation);

        if(syndromeErrorValueLocation != -1) {
            DoubleMatrix2D codeWordMatrix = getCodeWord(syndromeErrorValueLocation, syndromeErrorValue, modifyCodeMsg);
            LOGGER.info("A hiba javítása után a kódszó: {}", codeWordMatrix);
        } else {
            LOGGER.info("Valami hiba van a számításban!");
        }
    }

    private DoubleMatrix2D getCodeWord(int syndromeErrorValueLocation, int syndromeErrorValue, DoubleMatrix2D modifyCodeMsg) {
        DoubleMatrix2D eMatrix = new DenseDoubleMatrix2D(1,getNValue());

        for (int i = 0; i < eMatrix.columns(); i++) {
            if(i == syndromeErrorValueLocation) {
                eMatrix.setQuick(0, i, syndromeErrorValue);
            } else {
                eMatrix.setQuick(0, i, 0);
            }
        }

        for (int i = 0; i < eMatrix.columns(); i++) {
            int value = (int) (modifyCodeMsg.getQuick(0, i) - eMatrix.getQuick(0, i));
            if (value < 0) {
                while (value < 0) {
                    value += qNum;
                }
            } else {
                value = value % qNum;
            }
            eMatrix.setQuick(0, i, value);
        }

        return eMatrix;
    }

    private int syndromeErrorValueLocation(int syndromeErrorValue, DoubleMatrix2D syndromeMatrix) {
        DoubleMatrix2D matrix2D = syndromeMatrix.viewDice();

        LOGGER.info("Parity matrix: {}", parityMatrix2D);
        LOGGER.info("Syndrome T matrix: {}", matrix2D);

        int index = -1;

        for (int i = 0; i < matrix2D.rows(); i++) {
            for (int j = 0; j < matrix2D.columns(); j++) {
                double value = (matrix2D.getQuick(i, j) * syndromeErrorValue) % qNum;
                matrix2D.setQuick(i, j, value);
            }
        }

        LOGGER.info("Syndrome T matrix * e(i): {}", matrix2D);

        for (int i = 0; i < parityMatrix2D.columns(); i++) {
            if (parityMatrix2D.getQuick(0, i) == matrix2D.getQuick(0, 0) && parityMatrix2D.getQuick(1, i) == matrix2D.getQuick(1, 0)) {
                index = i;
            }
        }

        return index;
    }

    private int syndromeErrorValue(DoubleMatrix2D syndromeMatrix) {
        int syndromeValue = 0;
        for (int i = 0; i < syndromeMatrix.columns(); i++) {
            if (syndromeMatrix.getQuick(0, i) != 0) {
                syndromeValue = (int) syndromeMatrix.getQuick(0, i);
                break;
            }
        }
        return syndromeValue;
    }

    private DoubleMatrix2D setSyndromeMatrix(DoubleMatrix2D modifyCodeMsg, Algebra algebra) {
        DoubleMatrix2D matrix2D = algebra.mult(modifyCodeMsg, parityMatrix2D.viewDice());

        for (int i = 0; i < matrix2D.columns(); i++) {
            int value = (int) (matrix2D.getQuick(0, i) % qNum);
            matrix2D.setQuick(0, i, value);
        }

        return matrix2D;
    }

    private DoubleMatrix2D modifyCodeWord(DoubleMatrix2D codeMsd) {
        Scanner scanner = new Scanner(System.in);
        boolean isTrue = false;

        while (!isTrue) {
            LOGGER.info("Add meg a kódszó azon koordinátájának helyét, amelyiket meg akarod változtatni 0 és {} között:", codeMsd.columns() - 1);
            int val = scanner.nextInt();

            if(val > codeMsd.columns() || val < 0) {
                LOGGER.info("Túl nagy vagy túl kicsi étéket adtál meg!");
            } else if (codeMsd.get(0, val) == 1) {
                codeMsd.setQuick(0, val, 0);
                isTrue = true;
            } else {
                codeMsd.setQuick(0, val, 1);
                isTrue = true;
            }
        }

        scanner.close();

        return codeMsd;
    }

    private DoubleMatrix2D setCodeWord(int[] msgWord, DoubleMatrix2D generatorMatrix, Algebra algebra) {
        DoubleMatrix2D msgWordMatrix = new DenseDoubleMatrix2D(1, getKValue());
        DoubleMatrix2D codeWord;
        for (int i = 0; i < msgWord.length; i++) {
            msgWordMatrix.setQuick(0, i, msgWord[i]);
        }

        LOGGER.info("Message word matrix: {}", msgWordMatrix);
        codeWord = algebra.mult(msgWordMatrix, generatorMatrix);

        for (int i = 0; i < codeWord.columns(); i++) {
            int value = (int) (codeWord.getQuick(0, i) % qNum);
            codeWord.setQuick(0, i, value);
        }

        return codeWord;
    }

    private int[] setMessageWord() {
        Scanner scanner = new Scanner(System.in);
        boolean isNotCodeMsg = false;
        int[] finalCodeMsg = new int[getKValue()];

        while (!isNotCodeMsg) {
            LOGGER.info("Add meg az üzenet szót (szóközzel elválasztva):");
            String input = scanner.nextLine();

            int[] msgWord = Arrays.stream(input.split("\\s+"))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            int[] isMsgWord = Arrays.stream(msgWord).filter(value -> value > 0 && value < getNValue()).toArray();

            if (isMsgWord.length < 1 || msgWord.length != getKValue()) {
                LOGGER.info("Rossz étékeket adtál meg. Próbáld újra.");
            } else {
                isNotCodeMsg = true;
                finalCodeMsg = msgWord;
            }
        }

        return finalCodeMsg;
    }

    private void getParityMatrix(List<List<Integer>> qVectorList) {
        List<List<Integer>> setList = new ArrayList<>();
        parityMatrix2D = new DenseDoubleMatrix2D(2, getNValue());

        boolean isFindFirst = false;

        while (!qVectorList.isEmpty()) {
            for (int i = 0; i < qVectorList.size(); i++) {
                List<Integer> list = qVectorList.getFirst();

                for (int j = 0; j < kValue; j++) {
                    int finalJ = j + 1;
                    List<Integer> res = list.stream().map(x -> (x * finalJ) % qNum).toList();

                    if (res.stream().anyMatch(x -> x == 1) && !isFindFirst) {
                        setList.add(res);
                        isFindFirst = true;
                    }

                    qVectorList.remove(res);
                }
                isFindFirst = false;
            }
        }

        for (int i = 0; i < getNValue(); i++) {
            List<Integer> list = setList.get(i);
            LOGGER.info("Kiválasztott {}. elem: {}", i + 1, list);
            int val1 = list.getFirst();
            int val2 = list.get(1);

            parityMatrix2D.set(0, i, val1);
            parityMatrix2D.set(1, i, val2);
        }
    }

    private List<List<Integer>> getQVectorList(List<Integer[]> qVectorList) {
        List<List<Integer>> result = new ArrayList<>();
        for (Integer[] integers : qVectorList) {
            List<Integer> list = Arrays.stream(integers).toList();
            result.add(list);
        }

        return result;
    }

    private void setNAndKValue() {
        int value = (((int) Math.pow(qNum, rNum)) - 1) / (qNum - 1);
        setNValue(value);

        int value2 = getNValue() - rNum;
        setKValue(value2);
    }

    private List<Integer[]> vectorQList() {
        List<Integer[]> matrix1DList = new ArrayList<>();

        for (int i = 0; i < qNum; i++) {
            for (int j = 0; j < qNum; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                Integer[] value = {i, j};
                matrix1DList.add(value);
            }
        }

        return matrix1DList;
    }

    public void setNValue(int nValue) {
        this.nValue = nValue;
    }

    public void setKValue(int kValue) {
        this.kValue = kValue;
    }

    public int getNValue() {
        return nValue;
    }

    public int getKValue() {
        return kValue;
    }
}
