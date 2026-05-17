package com.bajiezu.cloud.customer.app.client;

public interface OssPrivateFileService {

    void upload(String key, byte[] content, String mimeType);

    String generatePreviewUrl(String key, long expireSeconds);
}
