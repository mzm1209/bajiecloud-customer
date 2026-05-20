package com.bajiezu.cloud.customer.app.client.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
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
        return response.getUserId();
    }

    public AlipayPhoneInfo getPhone(String authCode) {
        AlipaySystemOauthTokenResponse response = exchangeToken(authCode);
        AlipayPhoneInfo info = new AlipayPhoneInfo();
        info.setOpenId(response.getUserId());
        // TODO 当前 SDK 版本未提供 AlipayUserPhoneGetRequest/AlipayUserPhoneGetResponse，待升级后补充手机号获取。
        return info;
    }

    private AlipaySystemOauthTokenResponse exchangeToken(String authCode) {
        try {
            AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
            request.setGrantType("authorization_code");
            request.setCode(authCode);
            return execute(request);
        } catch (AlipayApiException e) {
            log.error("alipay exchange token failed");
            throw exception(LOGIN_EXCEPTION);
        }
    }

    private AlipayClient miniappClient() {
        try {
            log.debug("开始创建 client");
            Object holder = applicationContext.getBean("alipayClientHolder");
            log.debug("alipayClientHolder："+holder);
            Method method = holder.getClass().getMethod("miniappClient");
            log.debug("miniappClient："+method);
            Object client = method.invoke(holder);
            if (client instanceof AlipayClient alipayClient) {
                return alipayClient;
            }
        } catch (Exception ignored) {
            // ignore and throw business error below
        }
        log.error("ALIPAY_MINIAPP_CLIENT_NOT_READY");
        throw exception(LOGIN_EXCEPTION);
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
