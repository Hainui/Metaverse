package com.metaverse.common.Utils;

import com.metaverse.common.exception.InvalidServerLocationException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ServerLocationValidator {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]$"
    );

    private static final Pattern DOMAIN_PATTERN = Pattern.compile(
            "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$"
    );

    /**
     * 校验区服地址集合：是否存在不合法的区服地址
     * 支持 http, https 和 ftp 协议
     *
     * @param serverLocation 区服地址集合
     */
    public static void validateServerLocations(List<String> serverLocation) {
        List<String> invalidLocations = new ArrayList<>();

        for (String location : serverLocation) {
            if (!isValidUrl(location) && !isValidDomain(location)) {
                invalidLocations.add(location);
            }
        }

        if (!invalidLocations.isEmpty()) {
            throw new InvalidServerLocationException(invalidLocations);
        }
    }

    private static boolean isValidUrl(String url) {
        return URL_PATTERN.matcher(url).matches();
    }

    private static boolean isValidDomain(String domain) {
        return DOMAIN_PATTERN.matcher(domain).matches();
    }
}