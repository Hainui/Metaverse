package com.metaverse.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.metaverse.common.Utils.JwtUtils;
import com.metaverse.common.Utils.RedisServer;
import com.metaverse.common.constant.UserConstant;
import com.metaverse.common.model.Result;
import com.metaverse.user.dto.MetaverseUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {

    private final RedisServer redisServer;

    @Override//目标方法运行前运行,返回true就是放行,返回false就是不放行
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        //1.获取请求url。
        String url = req.getRequestURL().toString();
        log.info("请求的url: {}", url);

        //2.判断请求url中是否包含login，如果包含，说明是注册或登录操作，放行。
        if (url.contains("login") || url.contains("registration")) {
            log.info("登录操作, 放行...");
            return true;
        }

        //3.获取请求头中的令牌（token） - token
        String jwt = req.getHeader("token");

        //4.判断令牌是否存在，如果不存在，返回错误结果（未登录）。
        if (!StringUtils.hasText(jwt)) {
            log.info("请求头token为空,返回未登录的信息");
            Result<Object> error = Result.error(Collections.singletonList("穿梭于虚拟与现实之间，请先登录您的元宇宙身份"));
            //本来要在controller里面转换json,现在手动转换 对象--json --------> 使用这个方法:阿里巴巴fastJSON
            resp.setContentType("application/json; charset=UTF-8");
            String notLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }


        //5.解析token，如果解析失败，返回错误结果（未登录）。
        try {
//            MetaverseUserInfo userInfo = (MetaverseUserInfo) JwtUtils.parseJWT(jwt).get(UserConstant.METAVERSE_USER);

            MetaverseUserInfo userInfo = JwtUtils.parseJwtToUserInfo(jwt, UserConstant.METAVERSE_USER);
            Long userId = userInfo.getId();
            if (!redisServer.validateToken(userId, jwt)) {
                log.error("token已经过期或被销毁");
                throw new IllegalArgumentException("登陆已过期请重新登陆");
            }
        } catch (Exception e) {
            log.error("解析令牌失败，错误信息：{}", e.getMessage());
            log.info("解析令牌失败, 返回未登录错误信息");
            Result<Object> error = Result.error(Collections.singletonList("穿梭于虚拟与现实之间，请先登录您的元宇宙身份"));
            resp.setContentType("application/json; charset=UTF-8");
            String notLogin = JSONObject.toJSONString(error);
            resp.getWriter().write(notLogin);
            return false;
        }

        //6.放行。
        log.info("令牌合法, 放行");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
