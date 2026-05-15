package com.bajiezu.cloud.customer.app.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Configuration
public class AppTokenFilterConfig {

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        Object filter = resolveAppTokenAuthenticationFilter();
        if (filter == null) {
            return;
        }
        List<String> permitAllPaths = Arrays.asList(
                "/app/auth/alipay-login",
                "/app/sms/send",
                "/app/auth/mobile-login"
        );
        invokeSetter(filter, "setPermitAllPaths", permitAllPaths);
        invokeSetter(filter, "setNoNeedLoginPath", permitAllPaths);
    }

    private Object resolveAppTokenAuthenticationFilter() {
        for (Object bean : applicationContext.getBeansOfType(Object.class).values()) {
            if (bean.getClass().getSimpleName().contains("AppTokenAuthenticationFilter")) {
                return bean;
            }
        }
        return null;
    }

    private void invokeSetter(Object target, String methodName, List<String> value) {
        try {
            Method method = target.getClass().getMethod(methodName, List.class);
            method.invoke(target, value);
        } catch (Exception ignored) {
            // ignore and keep framework defaults
        }
    }
}
