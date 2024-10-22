package com.metaverse.card.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ProbabilityBasedSelection {

    public static <T> T selectElementBasedOnProbability(Map<T, BigDecimal> probabilityMap) {
        if (probabilityMap == null || probabilityMap.isEmpty()) {
            throw new IllegalArgumentException("Probability map cannot be null or empty.");
        }

        BigDecimal totalProbability = probabilityMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalProbability.compareTo(BigDecimal.ONE) != 0) {
            throw new IllegalArgumentException("Sum of probabilities must be 1.");
        }

        BigDecimal cumulativeProbability = BigDecimal.ZERO;
        Map<T, BigDecimal> cumulativeProbabilities = new HashMap<>();
        for (Map.Entry<T, BigDecimal> entry : probabilityMap.entrySet()) {
            cumulativeProbability = cumulativeProbability.add(entry.getValue());
            cumulativeProbabilities.put(entry.getKey(), cumulativeProbability);
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        BigDecimal randomProbability = BigDecimal.valueOf(random.nextDouble());

        for (Map.Entry<T, BigDecimal> entry : cumulativeProbabilities.entrySet()) {
            if (randomProbability.compareTo(entry.getValue()) <= 0) {
                return entry.getKey();
            }
        }

        return cumulativeProbabilities.keySet().iterator().next();
    }


    /**
     * 从列表中随机返回一个元素。
     *
     * @param list 需要从中随机选择元素的列表
     * @param <T>  列表中元素的类型
     * @return 随机选择的元素
     */
    public static <T> T selectRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List cannot be null or empty.");
        }

        // 生成随机索引
        int index = ThreadLocalRandom.current().nextInt(list.size());

        // 返回对应的元素
        return list.get(index);
    }
}