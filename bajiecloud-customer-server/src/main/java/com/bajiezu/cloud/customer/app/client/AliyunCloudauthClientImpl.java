package com.bajiezu.cloud.customer.app.client;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.cloudauth20190307.AsyncClient;
import com.aliyun.sdk.service.cloudauth20190307.models.VerifyMaterialRequest;
import com.aliyun.sdk.service.cloudauth20190307.models.VerifyMaterialResponse;
import com.bajiezu.cloud.customer.app.config.AliyunCloudauthProperties;
import com.bajiezu.cloud.customer.utils.JacksonUtil;
import darabonba.core.client.ClientOverrideConfiguration;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Slf4j
@Component
public class AliyunCloudauthClientImpl implements AliyunCloudauthClient {
    @Resource
    private AliyunCloudauthProperties properties;

    @Override
    public AliyunVerifyMaterialResult verifyMaterial(String realName, String idCard, String frontUrl, String backUrl) {
        AliyunVerifyMaterialResult result = new AliyunVerifyMaterialResult();
        if (!Boolean.TRUE.equals(properties.getEnabled())) {
            result.setSuccess(false); result.setCode("DISABLED"); result.setMessage("cloudauth disabled");
            return result;
        }
        try {
            Assert.hasText(properties.getAccessKeyId(), "aliyun.cloudauth.accessKeyId must not be blank");
            Assert.hasText(properties.getAccessKeySecret(), "aliyun.cloudauth.accessKeySecret must not be blank");
            StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                    .accessKeyId(properties.getAccessKeyId()).accessKeySecret(properties.getAccessKeySecret()).build());
            try (AsyncClient client = AsyncClient.builder().region(properties.getRegion()).credentialsProvider(provider)
                    .overrideConfiguration(ClientOverrideConfiguration.create().setEndpointOverride(properties.getEndpoint())).build()) {
                VerifyMaterialRequest request = VerifyMaterialRequest.builder()
                        .productCode(properties.getProductCode())
                        .sceneId(properties.getSceneId())
                        .certName(realName)
                        .certNo(idCard)
                        .idCardFrontImageUrl(frontUrl)
                        .idCardBackImageUrl(backUrl)
                        .build();
                VerifyMaterialResponse resp = client.verifyMaterial(request).get();
                result.setRequestId(resp.getBody() == null ? null : resp.getBody().getRequestId());
                result.setRawResult(JacksonUtil.obj2Str(resp));
                String code = resp.getBody() == null ? null : resp.getBody().getCode();
                result.setCode(code);
                result.setMessage(resp.getBody() == null ? null : resp.getBody().getMessage());
                result.setSuccess("200".equals(code) || "Success".equalsIgnoreCase(code));
            }
        } catch (Exception ex) {
            result.setSuccess(false); result.setCode("EXCEPTION"); result.setMessage("阿里云认证异常或服务暂不可用");
            log.warn("verifyMaterial failed, reqId={}, err={}", result.getRequestId(), ex.getClass().getSimpleName());
        }
        return result;
    }
}
