package com.bajiezu.cloud.customer.app.config;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.ocr_api20210707.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration
@EnableConfigurationProperties(AliyunOcrProperties.class)
public class AliyunOcrConfig {

    @Bean(destroyMethod = "close")
    public AsyncClient aliyunOcrAsyncClient(AliyunOcrProperties properties) {
        Assert.hasText(properties.getAccessKeyId(), "aliyun.ocr.accessKeyId must not be blank");
        Assert.hasText(properties.getAccessKeySecret(), "aliyun.ocr.accessKeySecret must not be blank");
        Assert.hasText(properties.getEndpoint(), "aliyun.ocr.endpoint must not be blank");

        StaticCredentialProvider provider = StaticCredentialProvider.create(
                Credential.builder()
                        .accessKeyId(properties.getAccessKeyId())
                        .accessKeySecret(properties.getAccessKeySecret())
                        .build()
        );

        return AsyncClient.builder()
                .region(properties.getRegion())
                .credentialsProvider(provider)
                .overrideConfiguration(
                        ClientOverrideConfiguration.create()
                                .setEndpointOverride(properties.getEndpoint())
                )
                .build();
    }
}
