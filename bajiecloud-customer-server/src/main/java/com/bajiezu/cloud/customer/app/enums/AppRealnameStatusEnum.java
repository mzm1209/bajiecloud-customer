package com.bajiezu.cloud.customer.app.enums;

public enum AppRealnameStatusEnum {
    UNREALNAME(0),
    CERTIFYING(1),
    REALNAME_SUCCESS(2),
    REALNAME_FAIL(3);

    private final Integer code;

    AppRealnameStatusEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
