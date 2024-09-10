package com.metaverse.common.Utils;


import com.metaverse.common.exception.InvalidServerLocationException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PermissionCodeValidator {

    private static final Pattern PERMISSION_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9]{6}$");

    /**
     * 校验权限码集合：是否存在不合法的权限码
     *
     * @param permissionCodes 权限码集合
     */
    public static void validatePermissionCodes(List<String> permissionCodes) {
        List<String> invalidCodes = new ArrayList<>();

        for (String code : permissionCodes) {
            if (!isValidPermissionCode(code)) {
                if (invalidCodes.isEmpty()) {
                    invalidCodes.add("存在不合法的权限码：");
                }
                invalidCodes.add(code);
            }
        }

        if (!invalidCodes.isEmpty()) {
            throw new InvalidServerLocationException(invalidCodes);
        }
    }

    private static boolean isValidPermissionCode(String permissionCode) {
        return PERMISSION_CODE_PATTERN.matcher(permissionCode).matches();
    }
}