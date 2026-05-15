package com.bajiezu.cloud.customer.app.client.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.bajiezu.cloud.customer.app.client.alipay.dto.AlipayPhoneInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;

@Slf4j
@Component
public class AlipayLoginGateway {

    @Resource
    private ApplicationContext applicationContext;

    public String getOpenId(String authCode) {
        AlipaySystemOauthTokenResponse response = exchangeToken(authCode);
        return response.getOpenId();
    }

    public AlipayPhoneInfo getPhone(String authCode) {
        AlipaySystemOauthTokenResponse response = exchangeToken(authCode);
        AlipayPhoneInfo info = new AlipayPhoneInfo();
        info.setOpenId(response.getOpenId());
        // TODO 当前依赖版本未提供 AlipayUserPhoneGetRequest/Response，后续升级 SDK 后补充手机号授权获取。
        return info;
    }

    private AlipaySystemOauthTokenResponse exchangeToken(String authCode) {
        try {
            AlipayClient client = miniappClient();
            AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
            request.setGrantType("authorization_code");
            request.setCode(authCode);
            AlipaySystemOauthTokenResponse response = client.certificateExecute(request);
            if (response == null || !response.isSuccess()) {
                throw exception(LOGIN_EXCEPTION);
            }
            return response;
        } catch (AlipayApiException e) {
            log.error("alipay exchange token failed, openIdSuffix={}", suffix(authCode));
            throw exception(LOGIN_EXCEPTION);
        }
    }

    private AlipayClient miniappClient() {
        try {
            Object holder = applicationContext.getBean("alipayClientHolder");
            Method method = holder.getClass().getMethod("miniappClient");
            Object client = method.invoke(holder);
            if (client instanceof AlipayClient alipayClient) {
                return alipayClient;
            }
        } catch (Exception ignored) {
            // ignore
        }
        log.error("ALIPAY_MINIAPP_CLIENT_NOT_READY");
            throw exception(LOGIN_EXCEPTION);
    }

    private String suffix(String openId) {
        if (openId == null || openId.length() <= 4) {
            return openId;
        }
        return openId.substring(openId.length() - 4);
    }
}
