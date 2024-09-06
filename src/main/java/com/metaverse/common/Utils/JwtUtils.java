package com.metaverse.common.Utils;

import com.metaverse.common.constant.UserConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

public class JwtUtils {

    //    生成Jwt令牌
    public static String generateJwt(Map<String, Object> claims) {
        return Jwts.builder()
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS256, UserConstant.SIGN_KEY)
                .setExpiration(new Date(System.currentTimeMillis() + UserConstant.EXPIRE))
                .compact();
    }

    //   解析Jwt令牌
    public static Claims parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(UserConstant.SIGN_KEY)
                .parseClaimsJws(jwt)
                .getBody();
    }

    public static Long getCurrentUserId() {
        String token = getToken();
        Claims claims = parseJWT(token);
        return claims.get(UserConstant.USER_ID, Long.class);
    }

    public static String getCurrentUserEmail() {
        String token = getToken();
        Claims claims = parseJWT(token);
        return claims.get(UserConstant.EMAIL, String.class);
    }

    public static Long getCurrentUserRegionId() {
        String token = getToken();
        Claims claims = parseJWT(token);
        return claims.get(UserConstant.REGION_ID, Long.class);
    }

    private static String getToken() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getHeader(UserConstant.TOKEN);
    }


    public class TokenBlackList {
        private static final String TOKEN_BLACKLIST_KEY = "jwt_blacklist";

        // 将令牌添加到黑名单
        public void addToBlacklist(String token) {
            try (Jedis jedis = new Jedis("localhost", 6379)) {
                jedis.sadd(TOKEN_BLACKLIST_KEY, token);
            }
        }

        // 检查令牌是否在黑名单中
        public boolean isInBlacklist(String token) {
            try (Jedis jedis = new Jedis("localhost", 6379)) {
                return jedis.sismember(TOKEN_BLACKLIST_KEY, token);
            }
        }
    }
}
