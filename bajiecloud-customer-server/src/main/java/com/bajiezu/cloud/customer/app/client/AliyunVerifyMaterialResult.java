package com.bajiezu.cloud.customer.app.client;

import lombok.Data;

@Data
public class AliyunVerifyMaterialResult {
    private boolean success;
    private String code;
    private String message;
    private String requestId;
    private String rawResult;
}
