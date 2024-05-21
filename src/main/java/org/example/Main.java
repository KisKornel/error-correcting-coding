package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

       Scanner scan = new Scanner(System.in);

       LOGGER.info("Menu");
       LOGGER.info("1 - Alfák kiszámítása");
       LOGGER.info("2 - Lineáris kódszámítás");
       LOGGER.info("3 - Hamming kódszámítás");
       LOGGER.info("4 - Reed Solomon kódszámítás (elvileg ez nem kell)");
       LOGGER.info("5 - 1 hibajavító kódszámítás");
       LOGGER.info("Add meg a menüpont számát: ");
       int num = Integer.parseInt(scan.nextLine());

       switch (num) {
           case 1:
               LOGGER.info("Add meg a Z feletti számot:");
               int zNum = scan.nextInt();

               primeFactors(zNum);

               break;
           case 2:
               LOGGER.info("Add meg a Z feletti számot:");
               int zNum2 = scan.nextInt();

               LOGGER.info("Add meg a mátrix sor számát:");
               int rows = scan.nextInt();

               LOGGER.info("Add meg a mátrix oszlop számát:");
               int columns = scan.nextInt();

               List<Integer> allALphaList = primeFactors(zNum2);

               linearCode(allALphaList, rows, columns, zNum2);

               break;
           case 3:
               LOGGER.info("Add meg a q elemszámát:");
               int qNum = scan.nextInt();

               LOGGER.info("Add meg az r paraméter értékét:");
               int rNum = scan.nextInt();

               List<Integer> allAlphaList2 = primeFactors(qNum);
               hammingCode(allAlphaList2, qNum,rNum);

               break;
           case 4:
               LOGGER.info("Add meg a Z feletti számot:");
               int zNum3 = scan.nextInt();

               LOGGER.info("Add meg a k üzenetszó hosszát:");
               int kNum = scan.nextInt();

               LOGGER.info("Add meg hány hibát akarsz javítani:");
               int errCorrectionNum = scan.nextInt();

               List<Integer> allAlphaList3 = primeFactors(zNum3);
               reedSolomonCode(allAlphaList3, kNum, errCorrectionNum, zNum3);

               break;
           case 5:
               LOGGER.info("Add meg a Z feletti számot:");
               int zNum4 = Integer.parseInt(scan.nextLine());

               LOGGER.info("Add meg az alpha értékét:");
               int alpha = Integer.parseInt(scan.nextLine());

               LOGGER.info("Add meg a vett jelet (szóközzel elválasztva):");
               String receivedWord = scan.nextLine();

               oneErrorCorrectionReedSolomonCode(zNum4, alpha, receivedWord);

               break;
           default:
               LOGGER.info("Nincs ilyen menüpont!");
               break;
       }

        scan.close();
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

    private static void oneErrorCorrectionReedSolomonCode(int zNum, int alpha, String receivedWord) {

        OneErrorCorrectionReedSolomonCode solomonCode = new OneErrorCorrectionReedSolomonCode(zNum, alpha, receivedWord);
        solomonCode.setReceivedSignal();
        solomonCode.setAlphaPow();
        solomonCode.calculateS1AndS2();
        solomonCode.errorLocation();
        solomonCode.errorValue();
        solomonCode.getCodeWord();

    }

}