package com.metaverse.common.constant;

public class PermissionConstant {

    public final static String SUPER_ADMINISTRATOR_PERMISSION = "*.*.*";


    public interface ResourceType {

        String PERMISSION = "PERMISSION";
        String PERMISSION_RELATIONSHIP = "PERMISSION_RELATIONSHIP";
        String REGION = "REGION";
        String USER = "USER";
    }

    public interface Action {
        String CREATE = "CREATE";
        String READ = "READ";
        String UPDATE = "UPDATE";
        String DELETE = "DELETE";
        String GRANT_PERMISSION = "GRANT_PERMISSION";
        String RESET_PERMISSION = "RESET_PERMISSION";
        String REVOKE_PERMISSION = "REVOKE_PERMISSION";

    }

    public interface Locator {
        String ACTIVATE = "ACTIVATE";//用户的状态,登陆的时候自动加入令牌中,如果封禁用户则只需要将该权限关掉就可以

    }

//    private static final int RESOURCE_TYPE_SIZE;
//    private static final int ACTIONS;
//    private static final int LOCATORS;
//    static {
//        RESOURCE_TYPE_SIZE = countConstants(ResourceType.class);
//        ACTIONS = countConstants(Action.class);
//        LOCATORS = countConstants(Locator.class);
//    }
//
//    /**
//     * 计算权限级别 authorizationLevel
//     *
//     * @param permissions 用户权限串数量
//     * @return authorizationLevel
//     */
//    public static String calculatePermissionCombinationSize(Integer permissions) {
//        int totalCombinations = RESOURCE_TYPE_SIZE * ACTIONS * LOCATORS;
//        BigDecimal result = BigDecimal.valueOf(totalCombinations)
//                .divide(BigDecimal.valueOf(permissions), 2, RoundingMode.DOWN)
//                .multiply(BigDecimal.valueOf(100));
//        DecimalFormat df = new DecimalFormat("#.00");
//        return df.format(result) + "%";
//    }
//
//    // 辅助方法，用于计算一个接口中常量的数量
//    private static int countConstants(Class<?> clazz) {
//        int count = 0;
//        for (Field field : clazz.getFields()) {
//            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
//                count++;
//            }
//        }
//        return count;
//    }
}