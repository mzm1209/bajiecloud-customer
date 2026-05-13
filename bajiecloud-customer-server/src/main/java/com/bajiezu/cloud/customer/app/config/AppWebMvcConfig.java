package com.bajiezu.cloud.customer.app.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@Configuration
public class AppWebMvcConfig implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;

    public AppWebMvcConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        HandlerInterceptor appLoginInterceptor = getAppLoginInterceptor();
        if (appLoginInterceptor == null) {
            return;
        }
        registry.addInterceptor(appLoginInterceptor)
                .addPathPatterns("/app/**")
                .excludePathPatterns(
                        "/app/auth/alipay-login",
                        "/app/auth/mobile-login",
                        "/app/sms/send",
                        "/app/callback/**"
                );
    }

    private HandlerInterceptor getAppLoginInterceptor() {
        Map<String, HandlerInterceptor> interceptorMap = applicationContext.getBeansOfType(HandlerInterceptor.class);
        for (HandlerInterceptor interceptor : interceptorMap.values()) {
            if ("AppLoginInterceptor".equals(interceptor.getClass().getSimpleName())) {
                return interceptor;
            }
        }
        return null;
    }
}
