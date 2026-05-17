package com.bajiezu.cloud.customer.app.client;

public interface AliyunCloudauthClient {
    AliyunVerifyMaterialResult verifyMaterial(String realName, String idCard, String idCardFrontFileKey, String idCardBackFileKey);
}
