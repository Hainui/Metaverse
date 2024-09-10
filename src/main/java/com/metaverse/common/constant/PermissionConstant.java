package com.metaverse.common.constant;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class PermissionConstant {

    private static final int RESOURCE_TYPE_SIZE;
    private static final int ACTIONS;
    private static final int LOCATORS;

    public interface ResourceType {
        static String USER = "USER";
        // 可以在这里添加更多的资源类型常量
        // static String OTHER_RESOURCE_TYPE = "OTHER_RESOURCE_TYPE";
    }

    public interface Action {
        static String USER = "USER";
        // 可以在这里添加更多的动作常量
        // static String OTHER_ACTION = "OTHER_ACTION";
    }

    public interface Locator {
        static String USER = "USER";
        // 可以在这里添加更多的定位器常量
        // static String OTHER_LOCATOR = "OTHER_LOCATOR";
    }

    static {
        RESOURCE_TYPE_SIZE = countConstants(ResourceType.class);
        ACTIONS = countConstants(Action.class);
        LOCATORS = countConstants(Locator.class);
    }

    /**
     * 计算权限级别 authorizationLevel
     *
     * @param permissions 用户权限串数量
     * @return authorizationLevel
     */
    public static String calculatePermissionCombinationSize(Integer permissions) {
        int totalCombinations = RESOURCE_TYPE_SIZE * ACTIONS * LOCATORS;
        BigDecimal result = BigDecimal.valueOf(totalCombinations)
                .divide(BigDecimal.valueOf(permissions), 2, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(100));
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(result) + "%";
    }

    // 辅助方法，用于计算一个接口中常量的数量
    private static int countConstants(Class<?> clazz) {
        int count = 0;
        for (Field field : clazz.getFields()) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                count++;
            }
        }
        return count;
    }
}