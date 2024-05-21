package org.example;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneErrorCorrectionReedSolomonCode {
    private static final Logger LOGGER = LoggerFactory.getLogger(OneErrorCorrectionReedSolomonCode.class);
    private final int zNum;
    private final int alpha;
    private final List<Integer> receivedSignal = new ArrayList<>();
    private final List<Integer> alphaPow = new ArrayList<>();
    private int s1;
    private int s2;
    private int errLoc;
    private int errValue;
    private final String receivedWord;

    public OneErrorCorrectionReedSolomonCode(int zNum, int alpha, String receivedWord) {
        this.zNum = zNum;
        this.alpha = alpha;
        this.receivedWord = receivedWord;
    }

    public void setReceivedSignal() {
        int[] codeWord = Arrays.stream(receivedWord.split("\\s+"))
                .mapToInt(Integer::parseInt)
                .toArray();

        for (int j : codeWord) {
            receivedSignal.add(j);
        }

    }

    public void setAlphaPow () {
        int a = 0;
        int k = 1;

        while (a != 1) {
            a = (int) (Math.pow(alpha, k)) % zNum;
            alphaPow.add(a);
            k++;
        }
    }

    public void calculateS1AndS2() {
        s1 = receivedSignal.getFirst();
        s2 = receivedSignal.getFirst();
        LOGGER.info("alpha {}", alpha);
        LOGGER.info("alphaPow {}", alphaPow);

        for(int i = 1; i < receivedSignal.size(); i++) {
            if(receivedSignal.get(i) == 0) {
                continue;
            }
            s1 += (receivedSignal.get(i) * alphaPow.get(i-1));
            s2 += (int) (receivedSignal.get(i) * Math.pow(alphaPow.get(i-1), 2));
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
        DoubleMatrix1D receivedCodeWord = new DenseDoubleMatrix1D(receivedSignal.size());

        for (int i = 0; i < receivedSignal.size(); i++) {
            receivedCodeWord.setQuick(i, receivedSignal.get(i));
        }

        LOGGER.info("Received word: {}", receivedCodeWord);

        DoubleMatrix1D eMatrix = new DenseDoubleMatrix1D(receivedSignal.size());

        eMatrix.setQuick(errLoc + 1, errValue);

        LOGGER.info("eMatrix: {}", eMatrix);

        DoubleMatrix1D codeWord = new DenseDoubleMatrix1D(receivedSignal.size());

        for (int i = 0; i < receivedSignal.size(); i++) {
            int value = (int) (receivedCodeWord.getQuick( i) - eMatrix.getQuick( i));
            if (value < 0) {
                while (value < 0) {
                    value += zNum;
                }
            } else {
                value = value % zNum;
            }
            codeWord.setQuick( i, value);
        }

       LOGGER.info("Code Word: {}", codeWord);
    }

}
