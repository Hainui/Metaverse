package com.metaverse.common.Utils;

import com.metaverse.common.exception.InvalidStrReqListException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PermissionStrValidator {

    private static final Pattern PERMISSION_CODE_PATTERN = Pattern.compile(
//            "^\\*\\.\\*\\.\\*|" + // 匹配通配符模式
//                    "([A-Z][A-Z_]*[A-Z])\\." + // 第一层，必须以大写字母开始和结束，并允许中间包含下划线
//                    "([A-Z][A-Z_]*[A-Z])\\." + // 第二层，同上
//                    "([A-Z][A-Z_]*[A-Z])$" // 第三层，同上
            // todo 包含一种两* 和 一* 的情况

            "^([A-Z][A-Z_]*[A-Z])\\.(?:([A-Z][A-Z_]*[A-Z])\\.|\\*\\.)([A-Z][A-Z_]*[A-Z]|\\*)$"
            // 结束非捕获组


    );

    /**
     * 校验权限串集合：是否存在不合法的权限串
     *
     * @param permissionStrs 权限串集合
     */
    public static void validatePermissionStrs(List<String> permissionStrs) {
        List<String> invalidStrs = new ArrayList<>();

        for (String code : permissionStrs) {
            if (!isValidPermissionCode(code)) {
                if (invalidStrs.isEmpty()) {
                    invalidStrs.add("存在不合法的权限串：");
                }
                invalidStrs.add(code);
            }
        }

        if (!invalidStrs.isEmpty()) {
            throw new InvalidStrReqListException(invalidStrs);
        }
    }

    private static boolean isValidPermissionCode(String permissionStr) {
        return PERMISSION_CODE_PATTERN.matcher(permissionStr).matches();
    }
}