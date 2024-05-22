package org.example;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneErrorCorrectionReedSolomonCode {
    private static final Logger LOGGER = LoggerFactory.getLogger(OneErrorCorrectionReedSolomonCode.class);
    private final int zNum;
    private final int alpha;
    private final List<Integer> alphaPow = new ArrayList<>();
    private final String receivedWord;
    private int[] codeWord;
    private int s1;
    private int s2;
    private int errLoc;
    private int errValue;

    public OneErrorCorrectionReedSolomonCode(int zNum, int alpha, String receivedWord) {
        this.zNum = zNum;
        this.alpha = alpha;
        this.receivedWord = receivedWord;
    }

    public void setReceivedSignal() {
        codeWord = Arrays.stream(receivedWord.split("\\s+"))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public void setAlphaPow () {
        BigInteger base = new BigInteger(String.valueOf(alpha));
        BigInteger result = BigInteger.valueOf(0);
        BigInteger one = BigInteger.valueOf(1);
        int k = 1;

        while (!result.equals(one)) {
            result = base.modPow(BigInteger.valueOf(k), BigInteger.valueOf(zNum));
            LOGGER.info("a: {}", result);
            alphaPow.add(result.intValue());
            k++;
        }
    }

    public void calculateS1AndS2() {
        s1 = codeWord[0];
        s2 = codeWord[0];
        LOGGER.info("alpha {}", alpha);
        LOGGER.info("alphaPow {}", alphaPow);

        for(int i = 1; i < codeWord.length; i++) {
            if(codeWord[i] == 0) {
                continue;
            }
            s1 += (codeWord[i] * alphaPow.get(i-1));
            s2 += (int) (codeWord[i] * Math.pow(alphaPow.get(i-1), 2));
        }


        s1 = s1 % zNum;
        s2 = s2 % zNum;

        LOGGER.info("S1: {}", s1);
        LOGGER.info("S2: {}", s2);
    }

    public void errorLocation() {
        int s1Alpha = s1;
        int s2Alpha = s2;
        int k = 2;

        if (((double) s2Alpha / s1Alpha) == 1.0) {
            LOGGER.info("A hiba helye az 1. helyen van");
            errLoc = 0;
        } else {
            while (s1Alpha != 1) {
                s1Alpha = (s1 * k) % zNum;
                s2Alpha = (s2 * k) % zNum;
                k++;
            }
            errLoc = alphaPow.indexOf(s2Alpha);
        }

    }

    public void errorValue() {
        int errV = 0;
        int k = 2;

        if (((double) s2 / s1) == 1.0) {
            errValue = s1;
        } else {
            while (errV != 1) {
                errV = ((alphaPow.get(errLoc) * k) % zNum);
                k++;
            }

            errValue = (s1 * (k - 1) % zNum);
        }

        LOGGER.info("Error location: {}", errLoc + 1);
        LOGGER.info("Error value: {}", errValue);
    }

    public void getCodeWord() {
        DoubleMatrix1D receivedCodeWord = new DenseDoubleMatrix1D(codeWord.length);

        for (int i = 0; i < codeWord.length; i++) {
            receivedCodeWord.setQuick(i, codeWord[i]);
        }

        LOGGER.info("Received word: {}", receivedCodeWord);

        DoubleMatrix1D eMatrix = new DenseDoubleMatrix1D(codeWord.length);

        eMatrix.setQuick(errLoc + 1, errValue);

        LOGGER.info("eMatrix: {}", eMatrix);

        DoubleMatrix1D codeWord2 = new DenseDoubleMatrix1D(codeWord.length);

        for (int i = 0; i < codeWord.length; i++) {
            int value = (int) (receivedCodeWord.getQuick( i) - eMatrix.getQuick( i));
            if (value < 0) {
                while (value < 0) {
                    value += zNum;
                }
            } else {
                value = value % zNum;
            }
            codeWord2.setQuick( i, value);
        }

       LOGGER.info("Code Word: {}", codeWord2);
    }

}
