package com.bajiezu.cloud.customer.app.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppWebMvcConfig implements WebMvcConfigurer {

    @Resource(name = "appLoginInterceptor")
    private HandlerInterceptor appLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(appLoginInterceptor)
                .addPathPatterns("/app/**")
                .excludePathPatterns(
                        "/app/auth/alipay-login",
                        "/app/auth/mobile-login",
                        "/app/sms/send",
                        "/app/callback/**"
                );
    }
}
