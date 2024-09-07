package com.metaverse.common.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metaverse.common.constant.UserConstant;
import com.metaverse.user.domain.region.dto.MetaverseRegionInfo;
import com.metaverse.user.dto.MetaverseUserInfo;
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

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static MetaverseUserInfo parseJwtToUserInfo(String jwt, String claimName) {
        Map<String, Object> jwtClaims = JwtUtils.parseJWT(jwt);
        Object metaverseUserClaim = jwtClaims.get(claimName);

        // 如果 claim 是 LinkedHashMap，尝试转换为 MetaverseUserInfo
        if (metaverseUserClaim instanceof Map) {
            try {
                return objectMapper.convertValue(metaverseUserClaim, MetaverseUserInfo.class);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Failed to parse JWT claim into MetaverseUserInfo", e);
            }
        } else {
            throw new IllegalArgumentException("JWT claim is not a Map");
        }
    }

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
        MetaverseUserInfo user = parseJWT(getToken()).get(UserConstant.METAVERSE_USER, MetaverseUserInfo.class);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("登录信息异常");
        }
        return user.getId();
    }

    public static String getCurrentUserEmail() {
        MetaverseUserInfo user = parseJWT(getToken()).get(UserConstant.METAVERSE_USER, MetaverseUserInfo.class);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("登录信息异常");
        }
        return user.getEmail();
    }

    public static MetaverseRegionInfo getCurrentUserRegion() {
        MetaverseUserInfo user = parseJWT(getToken()).get(UserConstant.METAVERSE_USER, MetaverseUserInfo.class);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("登录信息异常");
        }
        return user.getRegion();
    }

    private static String getToken() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getHeader(UserConstant.TOKEN);
    }
}
