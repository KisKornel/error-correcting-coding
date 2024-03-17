package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PrimitiveItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrimitiveItem.class);


    private final int number;
    private int alphaFinal;
    private final List<Integer> primeFactorsList = new ArrayList<>();
    private final List<Integer> bodyFill = new ArrayList<>();
    private final Map<Integer, Integer> elementCountMap = new HashMap<>();
    private final List<Integer> betaResults = new ArrayList<>();
    private final List<Integer> alphaResults = new ArrayList<>();
    private final List<Integer> alphaAllResults = new ArrayList<>();

    public PrimitiveItem(int number) {
        this.number = number;
    }

    public void primeFactors() {

        int num = number - 1;

        while (num % 2 == 0) {
            primeFactorsList.add(2);
            num /= 2;
        }

        for (int i = 3; i <= Math.sqrt(num); i += 2) {
            while (num % i == 0) {
                primeFactorsList.add(i);
                num /= i;
            }
        }

        if (num > 2) {
            primeFactorsList.add(num);
        }
    }

    public void primeFactorsBasisAndExponentNumber() {

        for (Integer element : primeFactorsList) {
            elementCountMap.put(element, elementCountMap.getOrDefault(element, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : elementCountMap.entrySet()) {
            LOGGER.info("Prim {}: {} db", entry.getKey(), entry.getValue());
        }

    }

    public void arrayListFill() {
        for (int i = 0; i < number; i++) {
            bodyFill.add(i);
        }

        LOGGER.info("Number list: {}", bodyFill);
    }

    public void isBetaNotNullFunc() {
        for (Map.Entry<Integer, Integer> entry : elementCountMap.entrySet()) {
            for(int j = 1; j < bodyFill.size(); j++) {
                if((Math.pow(j, (double) (number - 1) / entry.getKey()) - 1) % number != 0) {
                    betaResults.add(j);
                    break;
                }
            }
        }
    }

    public void alphaFunc() {
        List<Integer> key = elementCountMap.keySet().stream().toList();
        List<Integer> value = elementCountMap.values().stream().toList();

        for(int i = 0; i < betaResults.size(); i++) {
            int powNum = (int) Math.pow(betaResults.get(i), ((number - 1) / (Math.pow(key.get(i),value.get(i)))));

            alphaResults.add(powNum % number);
        }
    }

    public void alphaFinalFunc() {
        int alphaRes = 1;

        for(Integer alpha : alphaResults) {
            alphaRes *= alpha;
        }

        alphaFinal = alphaRes % number;
    }

    public void alphaAllResultsFunc() {
        alphaAllResults.add(alphaFinal);

        long res = alphaFinal;
        long mod;

        for(int i = 1; i < bodyFill.size() - 1; i++) {
           mod = res % number;
           res = alphaFinal * mod;
           alphaAllResults.add((int) (res % number));
        }
    }

    public List<Integer> getPrimeFactorsList() {
        return primeFactorsList;
    }

    public List<Integer> getBetaResults() {
        return betaResults;
    }

    public List<Integer> getAlphaResults() {
        return alphaResults;
    }

    public int getAlphaFinal() {
        return alphaFinal;
    }

    public List<Integer> getAlphaAllResults() {
        return alphaAllResults;
    }
}
