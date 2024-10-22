package com.metaverse.card.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProbabilityBasedSelection {

    public static <T> T selectElementBasedOnProbability(Map<T, BigDecimal> probabilityMap) {
        if (probabilityMap == null || probabilityMap.isEmpty()) {
            throw new IllegalArgumentException("Probability map cannot be null or empty.");
        }

        BigDecimal totalProbability = probabilityMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (!totalProbability.equals(BigDecimal.ONE)) {
            throw new IllegalArgumentException("Sum of probabilities must be 1.");
        }

        BigDecimal cumulativeProbability = BigDecimal.ZERO;
        Map<T, BigDecimal> cumulativeProbabilities = new HashMap<>();
        for (Map.Entry<T, BigDecimal> entry : probabilityMap.entrySet()) {
            cumulativeProbability = cumulativeProbability.add(entry.getValue());
            cumulativeProbabilities.put(entry.getKey(), cumulativeProbability);
        }

        Random random = new Random();
        BigDecimal randomProbability = BigDecimal.valueOf(random.nextDouble());

        for (Map.Entry<T, BigDecimal> entry : cumulativeProbabilities.entrySet()) {
            if (randomProbability.compareTo(entry.getValue()) <= 0) {
                return entry.getKey();
            }
        }

        return cumulativeProbabilities.keySet().iterator().next();
    }

//    public static void main(String[] args) {
//        Map<String, Double> probabilityMap = new HashMap<>();
//        probabilityMap.put("apple", 0.3);
//        probabilityMap.put("banana", 0.5);
//        probabilityMap.put("cherry", 0.2);
//
//        // 测试算法
//        for (int i = 0; i < 100; i++) {
//            Long selectedKey = selectElementBasedOnProbability(probabilityMap);
//            System.out.println(selectedKey);
//        }
//    }
}