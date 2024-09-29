package com.metaverse.common.config;


import com.metaverse.common.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration//配置类
public class WebConfig implements WebMvcConfigurer {
    //注入拦截器
    private final LoginCheckInterceptor loginCheckInterceptor;

    public WebConfig(LoginCheckInterceptor loginCheckInterceptor) {
        this.loginCheckInterceptor = loginCheckInterceptor;
    }


    @Override//注册拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        //"/**"表示拦截所有方法    excludePathPatterns("login")表示不拦截这个方法
        registry.addInterceptor(loginCheckInterceptor).addPathPatterns("/**").excludePathPatterns("/login");
    }
}
