package com.bajiezu.cloud.customer.app.client;

import com.aliyun.cloudauth20190307.Client;
import com.aliyun.cloudauth20190307.models.Id2MetaStandardVerifyRequest;
import com.aliyun.cloudauth20190307.models.Id2MetaStandardVerifyResponse;
import com.aliyun.cloudauth20190307.models.InitCardVerifyRequest;
import com.aliyun.cloudauth20190307.models.InitCardVerifyResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.bajiezu.cloud.customer.app.config.AliyunCloudauthProperties;
import com.bajiezu.cloud.customer.utils.JacksonUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.UUID;

@Slf4j
@Component
public class AliyunCloudauthClientImpl implements AliyunCloudauthClient {
    @Resource
    private AliyunCloudauthProperties properties;

    @Override
    public AliyunVerifyMaterialResult verifyMaterial(String realName, String idCard, String frontUrl, String backUrl) {
        AliyunVerifyMaterialResult result = new AliyunVerifyMaterialResult();
        if (!Boolean.TRUE.equals(properties.getEnabled())) {
            result.setSuccess(false);
            result.setCode("DISABLED");
            result.setMessage("cloudauth disabled");
            return result;
        }
        try {
            Assert.hasText(properties.getAccessKeyId(), "aliyun.cloudauth.accessKeyId must not be blank");
            Assert.hasText(properties.getAccessKeySecret(), "aliyun.cloudauth.accessKeySecret must not be blank");
            Client client = createClient();
            RuntimeOptions runtime = new RuntimeOptions();

            InitCardVerifyRequest initReq = new InitCardVerifyRequest()
                    .setMerchantBizId(UUID.randomUUID().toString().replace("-", ""))
                    .setCardType("IDENTITY_CARD")
                    .setModel("OCR_VERIFY")
                    .setVerifyMeta("ID_2_META")
                    .setCardPageNumber("2")
                    .setPictureSave("Y");
            InitCardVerifyResponse initResp = client.initCardVerifyWithOptions(initReq, runtime);

            Id2MetaStandardVerifyRequest verifyReq = new Id2MetaStandardVerifyRequest()
                    .setParamType("normal")
                    .setIdentifyNum(idCard)
                    .setUserName(realName);
            Id2MetaStandardVerifyResponse verifyResp = client.id2MetaStandardVerifyWithOptions(verifyReq, runtime);

            String initJson = JacksonUtil.obj2Str(initResp);
            String verifyJson = JacksonUtil.obj2Str(verifyResp);
            result.setRawResult("{\"initCardVerify\":" + initJson + ",\"id2MetaStandardVerify\":" + verifyJson + "}");
            result.setRequestId(initResp == null || initResp.getBody() == null ? null : initResp.getBody().getRequestId());

            String code = verifyResp == null || verifyResp.getBody() == null ? null : verifyResp.getBody().getCode();
            String bizCode = verifyResp == null || verifyResp.getBody() == null || verifyResp.getBody().getResultObject() == null ? null : verifyResp.getBody().getResultObject().getBizCode();
            result.setCode(code);
            result.setMessage(verifyResp == null || verifyResp.getBody() == null ? null : verifyResp.getBody().getMessage());
            result.setSuccess("200".equals(code) && "1".equals(bizCode));
        } catch (TeaException e) {
            result.setSuccess(false);
            result.setCode("EXCEPTION");
            result.setMessage("阿里云认证异常或服务暂不可用");
            log.warn("cloudauth tea exception, err={}", e.getMessage());
        } catch (Exception ex) {
            result.setSuccess(false);
            result.setCode("EXCEPTION");
            result.setMessage("阿里云认证异常或服务暂不可用");
            log.warn("cloudauth exception, err={}", ex.getClass().getSimpleName());
        }
        return result;
    }

    private Client createClient() throws Exception {
        com.aliyun.credentials.models.Config credentialConfig = new com.aliyun.credentials.models.Config();
        credentialConfig.setType("access_key");
        credentialConfig.setAccessKeyId(properties.getAccessKeyId());
        credentialConfig.setAccessKeySecret(properties.getAccessKeySecret());

        com.aliyun.credentials.Client credentialClient = new com.aliyun.credentials.Client(credentialConfig);
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setCredential(credentialClient);
        config.setEndpoint(properties.getEndpoint());
        return new Client(config);
    }
}
