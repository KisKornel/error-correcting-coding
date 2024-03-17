package org.example;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ReedSolomonCode {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReedSolomonCode.class);
    private final int kNum;
    private final int errCorrectionNum;
    private final int zNum;
    private final List<Integer> allALphaList;
    private DoubleMatrix2D generatorMatrix;
    private DoubleMatrix2D parityMatrix;

    public ReedSolomonCode(int kNum, int errCorrectionNum, int zNum, List<Integer> allALphaList) {
        this.kNum = kNum;
        this.errCorrectionNum = errCorrectionNum;
        this.zNum = zNum;
        this.allALphaList = allALphaList;
    }

    private int minErrCorrectionNum() {
        return  (2 * errCorrectionNum) + 1;
    }

    private int codeWordLength() {
        return (kNum - 1) + minErrCorrectionNum();
    }

    public void setGeneratorMatrix() {
        int n = codeWordLength();
        int alpha = allALphaList.getFirst();
        LOGGER.info("All Alpha: {}", allALphaList);
        LOGGER.info("Alpha: {}", alpha);
        generatorMatrix = new DenseDoubleMatrix2D(kNum, n);

        for (int i = 0; i < generatorMatrix.rows(); i++) {
            for (int j = 0; j < generatorMatrix.columns(); j++) {
                if(i == 0 || j == 0) {
                    generatorMatrix.setQuick(i, j, 1);
                } else {
                    int value = alphaPow(alpha, i, j);
                    generatorMatrix.setQuick(i, j, value);
                }
            }
        }
    }

    public void setParityMatrix() {
        int n = codeWordLength();
        int[] row = new int[n - kNum];
        int[] column = new int[n];

        for (int i = 0; i < n; i++) {
            if(i < n - kNum) {
                row[i] = i + 1;
            }
            column[i] = i;
        }

        parityMatrix = generatorMatrix.viewSelection(row, column);
    }

    private int alphaPow(int alpha, int rowNum, int columnNum) {
        int pow = rowNum * columnNum;
        int n = alpha;

        for (int i = 1; i < pow; i++) {
            n = (n * alpha) % zNum;
        }

        return n;
    }

    public DoubleMatrix2D getGeneratorMatrix() {
        return generatorMatrix;
    }

    public DoubleMatrix2D getParityMatrix() {
        return parityMatrix;
    }
}
