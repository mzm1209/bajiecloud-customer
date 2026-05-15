package com.bajiezu.cloud.customer.app.client.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserPhoneGetRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserPhoneGetResponse;
import com.bajiezu.cloud.customer.app.client.alipay.dto.AlipayPhoneInfo;
import com.bajiezu.cloud.framework.alipay.core.client.AlipayClientHolder;
import com.bajiezu.cloud.framework.alipay.config.AlipayProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;

@Slf4j
@Component
public class AlipayLoginGateway {

    @Resource
    private AlipayClientHolder alipayClientHolder;
    @Resource
    private AlipayProperties alipayProperties;

    public String getOpenId(String authCode) {
        AlipaySystemOauthTokenResponse response = exchangeToken(authCode);
        return response.getOpenId();
    }

    public AlipayPhoneInfo getPhone(String authCode) {
        AlipaySystemOauthTokenResponse response = exchangeToken(authCode);
        AlipayPhoneInfo info = new AlipayPhoneInfo();
        info.setOpenId(response.getOpenId());
        try {
            AlipayUserPhoneGetRequest phoneGetRequest = new AlipayUserPhoneGetRequest();
            phoneGetRequest.setCode(authCode);
            AlipayUserPhoneGetResponse phoneGetResponse = miniappClient().certificateExecute(phoneGetRequest);
            if (phoneGetResponse != null && phoneGetResponse.isSuccess()) {
                info.setMobile(phoneGetResponse.getMobile());
            }
        } catch (Exception e) {
            log.warn("alipay get phone failed, openIdSuffix={}", suffix(info.getOpenId()));
        }
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
            log.error("alipay exchange token failed, appId={}", alipayProperties.getMiniAppId());
            throw exception(LOGIN_EXCEPTION);
        }
    }

    private AlipayClient miniappClient() {
        AlipayClient client = alipayClientHolder.miniappClient();
        if (client == null) {
            log.error("ALIPAY_MINIAPP_CLIENT_NOT_READY, appId={}", alipayProperties.getMiniAppId());
            throw exception(LOGIN_EXCEPTION);
        }
        return client;
    }

    private String suffix(String openId) {
        if (openId == null || openId.length() <= 4) {
            return openId;
        }
        return openId.substring(openId.length() - 4);
    }
}
