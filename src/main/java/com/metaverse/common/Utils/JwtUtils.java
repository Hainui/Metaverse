package com.metaverse.common.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class JwtUtils {

    private static final String signKey = "xiaoze";//登陆密钥
    private static final Long expire = 86400000L;//令牌有效时长(24小时)

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

    public static Long getCurrentUserId() {
        String token = getToken();
        Claims claims = parseJWT(token);
        return claims.get("userId", Long.class);
    }

    public static String getCurrentUserEmail() {
        String token = getToken();
        Claims claims = parseJWT(token);
        return claims.get("Email", String.class);
    }

    public static Long getCurrentUserRegionId() {
        String token = getToken();
        Claims claims = parseJWT(token);
        return claims.get("regionId", Long.class);
    }

    private static String getToken() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getHeader("token");
    }
}
