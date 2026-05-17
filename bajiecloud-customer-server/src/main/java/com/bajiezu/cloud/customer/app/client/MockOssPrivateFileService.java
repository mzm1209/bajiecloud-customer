package com.bajiezu.cloud.customer.app.client;

import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class MockOssPrivateFileService implements OssPrivateFileService {

    @Override
    public void upload(String key, byte[] content, String mimeType) {
        // mock upload
    }

    @Override
    public String generatePreviewUrl(String key, long expireSeconds) {
        return "https://oss-sign-url/mock?key=" + URLEncoder.encode(key, StandardCharsets.UTF_8) + "&exp=" + expireSeconds;
    }
}
