package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

       Scanner scan = new Scanner(System.in);
/*
        LOGGER.info("Add meg a Z feletti számot:");
        int zNum = scan.nextInt();

        LOGGER.info("Add meg a mátrix sor számát:");
        int rows = scan.nextInt();

        LOGGER.info("Add meg a mátrix oszlop számát:");
        int columns = scan.nextInt();

        List<Integer> allALphaList = primeFactors(zNum);

        linearCode(allALphaList, rows, columns, zNum);


        LOGGER.info("Add meg a q elemszámát:");
        int qNum = scan.nextInt();

        LOGGER.info("Add meg az r paraméter értékét:");
        int rNum = scan.nextInt();

        LOGGER.info("Add meg a k üzenetszó hosszát:");
        int kNum = scan.nextInt();

        LOGGER.info("Add meg hány hibát akarsz javítani:");
        int errCorrectionNum = scan.nextInt();

        LOGGER.info("Add meg a Z feletti számot:");
        int zNum = scan.nextInt();

        List<Integer> allALphaList = primeFactors(zNum);
        reedSalamonCode(allALphaList, kNum, errCorrectionNum, zNum);
*/
        LOGGER.info("Add meg a Z feletti számot:");
        int zNum = Integer.parseInt(scan.nextLine());

        LOGGER.info("Add meg az alpha 1. lehetséges értékét (a polinomban az első x-a értékből következik általában):");
        int alpha1 = Integer.parseInt(scan.nextLine());

        LOGGER.info("Add meg az alpha 2. lehetséges értékét (a polinomban az első x-a értékből következik általában):");
        int alpha2 = Integer.parseInt(scan.nextLine());

        LOGGER.info("Add meg a vett jelet (szóközzel elválasztva):");
        String receivedWord = scan.nextLine();

        scan.close();

        oneErrorCorrectionReedSolomonCode(zNum, alpha1, alpha2, receivedWord);
    }

    private static List<Integer> primeFactors(int zNum) {
        PrimitiveItem primitiveItem = new PrimitiveItem(zNum);

        primitiveItem.arrayListFill();
        primitiveItem.primeFactors();
        LOGGER.info("Prime list: {}", primitiveItem.getPrimeFactorsList());
        primitiveItem.primeFactorsBasisAndExponentNumber();
        primitiveItem.isBetaNotNullFunc();
        LOGGER.info("Beta list: {}", primitiveItem.getBetaResults());
        primitiveItem.alphaFunc();
        LOGGER.info("Alpha results: {}", primitiveItem.getAlphaResults());
        primitiveItem.alphaFinalFunc();
        LOGGER.info("Alpha final result: {}", primitiveItem.getAlphaFinal());
        primitiveItem.alphaAllResultsFunc();
        LOGGER.info("Alpha all results: {}", primitiveItem.getAlphaAllResults());

        return primitiveItem.getAlphaAllResults();
    }

    private static void linearCode(List<Integer> allAlphaList, int rows, int columns, int zNum) {
        LinearCode linearCode = new LinearCode(rows,columns, zNum, allAlphaList);

        linearCode.setGeneratorMatrix();
        linearCode.setPivotTable();
        linearCode.setPivotTableFull();
        linearCode.setParityMatrix();
        linearCode.getMultiplicationOfGeneratorMatrixByParityMatrix();
    }

    private static void hammingCode(List<Integer> allAlphaList, int qNum, int rNum) {
        HammingCode hammingCode = new HammingCode(qNum, rNum, allAlphaList);
        hammingCode.getHammingCode();

    }

    private static void reedSolomonCode(List<Integer> allAlphaList, int kNum, int errCorrectionNum, int zNum) {
        ReedSolomonCode reedSolomonCode = new ReedSolomonCode(kNum,errCorrectionNum, zNum, allAlphaList);
        reedSolomonCode.setGeneratorMatrix();
        LOGGER.info("Generator matrix: {}", reedSolomonCode.getGeneratorMatrix());
        reedSolomonCode.setParityMatrix();
        LOGGER.info("Parity matrix: {}", reedSolomonCode.getParityMatrix());
    }

    private static void oneErrorCorrectionReedSolomonCode(int zNum, int alpha1, int alpha2, String receivedWord) {
        int alpha = alpha1;
        if(Math.pow(alpha1, 2) % zNum != alpha2) {
            alpha = alpha2;
        }

        OneErrorCorrectionReedSolomonCode solomonCode = new OneErrorCorrectionReedSolomonCode(zNum, alpha, receivedWord);
        solomonCode.setReceivedSignal();
        solomonCode.setAlphaPow();
        solomonCode.calculateS1AndS2();
        solomonCode.errorLocation();
        solomonCode.errorValue();
        solomonCode.getCodeWord();
    }
}