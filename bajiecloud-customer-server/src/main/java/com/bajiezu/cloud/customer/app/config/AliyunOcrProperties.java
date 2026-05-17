package com.bajiezu.cloud.customer.app.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "aliyun.ocr")
public class AliyunOcrProperties {

    private String accessKeyId;

    private String accessKeySecret;

    private String endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";

    private String region = "cn-hangzhou";
}
