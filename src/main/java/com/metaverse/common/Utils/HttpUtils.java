package com.metaverse.common.Utils;

import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServletRequest;

public class HttpUtils {

    /**
     * 获取客户端的IP地址。
     *
     * @param request 请求对象
     * @return 客户端IP地址
     */
    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ipIsUnknown(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ipIsUnknown(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipIsUnknown(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipIsUnknown(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipIsUnknown(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ipIsUnknown(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于X-Forwarded-For，第一个IP是非代理客户端的真实IP
        if (ip != null && ip.contains(StrUtil.COMMA)) {
            ip = ip.substring(0, ip.indexOf(StrUtil.COMMA)).trim();
        }
        return ip;
    }

    public static boolean ipIsUnknown(String ip) {
        return ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip);
    }
}
