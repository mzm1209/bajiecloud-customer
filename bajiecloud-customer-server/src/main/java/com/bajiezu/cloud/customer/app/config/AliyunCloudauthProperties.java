package com.bajiezu.cloud.customer.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aliyun.cloudauth")
public class AliyunCloudauthProperties {
    private String accessKeyId;
    private String accessKeySecret;
    private String region = "cn-hangzhou";
    private String endpoint = "cloudauth.aliyuncs.com";
    private String productCode = "ID_PRO";
    private String sceneId;
    private Boolean enabled = Boolean.TRUE;
}
