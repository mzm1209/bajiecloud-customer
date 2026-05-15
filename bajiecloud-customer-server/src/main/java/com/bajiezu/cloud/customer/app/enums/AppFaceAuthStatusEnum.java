package com.bajiezu.cloud.customer.app.enums;

public enum AppFaceAuthStatusEnum {
    UNAUTH(0),
    AUTHING(1),
    AUTH_SUCCESS(2),
    AUTH_FAIL(3);

    private final Integer code;

    AppFaceAuthStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
