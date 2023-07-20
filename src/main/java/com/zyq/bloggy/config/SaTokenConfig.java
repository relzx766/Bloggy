package com.zyq.bloggy.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    private List<String> excludePath = new ArrayList<>();

    //登录验证
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        excludePath.add("/user/login");
        excludePath.add("/user/register");
        excludePath.add("/user/verify");
        excludePath.add("/default/**");
        excludePath.add("/image/**");
        excludePath.add("/video/**");
        registry.addInterceptor(new SaInterceptor(handle -> {
                    StpUtil.checkLogin();
                }))
                .addPathPatterns("/**")
                .excludePathPatterns(excludePath);
    }
}
