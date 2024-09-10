package com.metaverse.common.Utils;


import com.metaverse.common.exception.InvalidServerLocationException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PermissionStrValidator {

    private static final Pattern PERMISSION_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9]{6}$");

    /**
     * 校验权限码集合：是否存在不合法的权限码
     *
     * @param permissionStrs 权限码集合
     */
    public static void validatePermissionStrs(List<String> permissionStrs) {
        List<String> invalidStrs = new ArrayList<>();

        for (String code : permissionStrs) {
            if (!isValidPermissionCode(code)) {
                if (invalidStrs.isEmpty()) {
                    invalidStrs.add("存在不合法的权限码：");
                }
                invalidStrs.add(code);
            }
        }

        if (!invalidStrs.isEmpty()) {
            throw new InvalidServerLocationException(invalidStrs);
        }
    }

    private static boolean isValidPermissionCode(String permissionStr) {
        return PERMISSION_CODE_PATTERN.matcher(permissionStr).matches();
    }
}