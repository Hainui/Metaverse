package com.metaverse.common.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;


public class JwtUtils {

    private static String signKey = "xiaoze";//登陆密钥
    private static Long expire = 86400000L;//令牌有效时长(24小时)

    //    生成Jwt令牌
    public static String generateJwt(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, signKey)
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .compact();
    }

    //   解析Jwt令牌
    public static Claims parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(signKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static Long getCurrentUserId(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.get("userId", Long.class);
    }

    public static String getCurrentUserEmail(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.get("Email", String.class);
    }

    public static Long getCurrentUserRegionId(String jwt) {
        Claims claims = parseJWT(jwt);
        return claims.get("regionId", Long.class);
    }
}
