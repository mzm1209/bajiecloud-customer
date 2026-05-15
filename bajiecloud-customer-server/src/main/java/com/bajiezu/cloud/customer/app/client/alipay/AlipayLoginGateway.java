package com.bajiezu.cloud.customer.app.client.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserPhoneGetRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserPhoneGetResponse;
import com.bajiezu.cloud.alipay.AlipayClientHolder;
import com.bajiezu.cloud.alipay.AlipayProperties;
import com.bajiezu.cloud.customer.app.client.alipay.dto.AlipayPhoneInfo;
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
        return response.getUserId();
    }

    public AlipayPhoneInfo getPhone(String authCode) {
        AlipaySystemOauthTokenResponse response = exchangeToken(authCode);
        AlipayPhoneInfo info = new AlipayPhoneInfo();
        info.setOpenId(response.getUserId());
        String accessToken = response.getAccessToken();
        if (accessToken == null) {
            return info;
        }
        try {
            AlipayUserPhoneGetRequest request = new AlipayUserPhoneGetRequest();
            AlipayUserPhoneGetResponse phoneResp = miniappClient().execute(request, accessToken);
            if (phoneResp != null && phoneResp.isSuccess()) {
                info.setMobile(phoneResp.getMobile());
            }
        } catch (AlipayApiException e) {
            log.warn("alipay phone get error, openIdSuffix={}", suffix(info.getOpenId()));
        }
        return info;
    }

    private AlipaySystemOauthTokenResponse exchangeToken(String authCode) {
        try {
            AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
            request.setGrantType("authorization_code");
            request.setCode(authCode);
            return execute(request);
        } catch (AlipayApiException e) {
            log.error("alipay exchange token failed, appId={}", alipayProperties.getMiniapp().getAppId());
            throw exception(LOGIN_EXCEPTION);
        }
    }

    private AlipayClient miniappClient() {
        AlipayClient client = alipayClientHolder.miniappClient();
        if (client == null) {
            log.error("ALIPAY_MINIAPP_CLIENT_NOT_READY, appId={}", alipayProperties.getMiniapp().getAppId());
            throw exception(LOGIN_EXCEPTION);
        }
        return client;
    }

    private <R extends AlipayResponse> R execute(AlipayRequest<R> request) throws AlipayApiException {
        R response = miniappClient().execute(request);
        if (response == null || !response.isSuccess()) {
            throw exception(LOGIN_EXCEPTION);
        }
        return response;
    }

    private String suffix(String openId) {
        if (openId == null || openId.length() <= 4) {
            return openId;
        }
        return openId.substring(openId.length() - 4);
    }
}
