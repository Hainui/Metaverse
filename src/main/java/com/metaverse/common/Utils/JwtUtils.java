package com.metaverse.common.Utils;

import com.metaverse.common.constant.UserConstant;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.domain.region.domain.MetaverseRegion;
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
        MetaverseUser user = parseJWT(getToken()).get(UserConstant.METAVERSE_USER, MetaverseUser.class);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("登录信息异常");
        }
        return user.getId();
    }

    public static String getCurrentUserEmail() {
        MetaverseUser user = parseJWT(getToken()).get(UserConstant.METAVERSE_USER, MetaverseUser.class);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("登录信息异常");
        }
        return user.getEmail();
    }

    public static MetaverseRegion getCurrentUserRegion() {
        MetaverseUser user = parseJWT(getToken()).get(UserConstant.METAVERSE_USER, MetaverseUser.class);
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
