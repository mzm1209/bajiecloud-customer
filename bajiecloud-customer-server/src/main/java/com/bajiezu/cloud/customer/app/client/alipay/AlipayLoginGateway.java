package com.bajiezu.cloud.customer.app.client.alipay;

import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserPhoneGetRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserPhoneGetResponse;
import com.bajiezu.cloud.customer.app.client.alipay.dto.AlipayPhoneInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;

@Slf4j
@Component
public class AlipayLoginGateway {

    @Autowired
    private ApplicationContext applicationContext;

    public String getOpenId(String authCode) {
        try {
            AlipayClient client = miniappClient();
            AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
            request.setGrantType("authorization_code");
            request.setCode(authCode);
            AlipaySystemOauthTokenResponse response = client.certificateExecute(request);
            if (response == null || !response.isSuccess()) {
                throw exception(LOGIN_EXCEPTION);
            }
            return response.getOpenId();
        } catch (AlipayApiException e) {
            log.error("alipay getOpenId failed", e);
            throw exception(LOGIN_EXCEPTION);
        }
    }

    public AlipayPhoneInfo getPhone(String authCode) {
        try {
            AlipayClient client = miniappClient();
            AlipaySystemOauthTokenRequest tokenRequest = new AlipaySystemOauthTokenRequest();
            tokenRequest.setGrantType("authorization_code");
            tokenRequest.setCode(authCode);
            AlipaySystemOauthTokenResponse tokenResponse = client.certificateExecute(tokenRequest);
            if (tokenResponse == null || !tokenResponse.isSuccess()) {
                throw exception(LOGIN_EXCEPTION);
            }

            AlipayUserPhoneGetRequest request = new AlipayUserPhoneGetRequest();
            AlipayUserPhoneGetResponse response = client.certificateExecute(request, tokenResponse.getAccessToken());
            AlipayPhoneInfo info = new AlipayPhoneInfo();
            info.setOpenId(tokenResponse.getOpenId());
            if (response != null && response.isSuccess()) {
                info.setMobile(response.getMobile());
            }
            return info;
        } catch (AlipayApiException e) {
            log.error("alipay getPhone failed", e);
            throw exception(LOGIN_EXCEPTION);
        }
    }

    private AlipayClient miniappClient() {
        try {
            Object holder = applicationContext.getBean("alipayClientHolder");
            Method method = holder.getClass().getMethod("miniappClient");
            Object client = method.invoke(holder);
            if (!(client instanceof AlipayClient)) {
                throw exception(LOGIN_EXCEPTION);
            }
            return (AlipayClient) client;
        } catch (Exception e) {
            log.error("ALIPAY_MINIAPP_CLIENT_NOT_READY", e);
            throw exception(LOGIN_EXCEPTION);
        }
    }
}
