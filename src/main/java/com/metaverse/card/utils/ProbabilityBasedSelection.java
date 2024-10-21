package com.metaverse.card.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProbabilityBasedSelection {

    /**
     * 根据概率分布选择一个键。
     *
     * @param probabilityMap 包含键和对应概率的 HashMap
     * @return 根据概率分布随机选择的键
     */
    public static String selectKeyBasedOnProbability(Map<String, Double> probabilityMap) {
        if (probabilityMap == null || probabilityMap.isEmpty()) {
            throw new IllegalArgumentException("Probability map cannot be null or empty.");
        }

        // 归一化概率分布
        double totalProbability = probabilityMap.values().stream().mapToDouble(Double::doubleValue).sum();
        if (Math.abs(totalProbability - 1.0) > 0.00001) {
            throw new IllegalArgumentException("Sum of probabilities must be 1.");
        }

        // 计算累积概率
        double cumulativeProbability = 0.0;
        Map<String, Double> cumulativeProbabilities = new HashMap<>();
        for (Map.Entry<String, Double> entry : probabilityMap.entrySet()) {
            cumulativeProbability += entry.getValue();
            cumulativeProbabilities.put(entry.getKey(), cumulativeProbability);
        }

        // 生成随机数
        Random random = new Random();
        double randomProbability = random.nextDouble();

        // 查找键
        for (Map.Entry<String, Double> entry : cumulativeProbabilities.entrySet()) {
            if (entry.getValue() >= randomProbability) {
                return entry.getKey();
            }
        }

        // 如果没有找到，返回最后一个键（理论上不应该到达这里）
        return cumulativeProbabilities.keySet().iterator().next();
    }

    public static void main(String[] args) {
        Map<String, Double> probabilityMap = new HashMap<>();
        probabilityMap.put("apple", 0.3);
        probabilityMap.put("banana", 0.5);
        probabilityMap.put("cherry", 0.2);

        // 测试算法
        for (int i = 0; i < 100; i++) {
            String selectedKey = selectKeyBasedOnProbability(probabilityMap);
            System.out.println(selectedKey);
        }
    }
}