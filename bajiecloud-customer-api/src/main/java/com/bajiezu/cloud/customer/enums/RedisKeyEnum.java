package com.bajiezu.cloud.customer.enums;

import lombok.Getter;

import java.util.concurrent.TimeUnit;

@Getter
public enum RedisKeyEnum implements IRedisKey {

    CUSTOMER_BASE_INFO("customer_base_info:%s", TimeUnit.DAYS, 7, "客户基础信息"),





    ;


    private final String key;
    private final TimeUnit timeUnit;
    private final long expireTime;
    private final String desc;

    RedisKeyEnum(String key, TimeUnit timeUnit, long expireTime, String desc) {
        this.key = key;
        this.timeUnit = timeUnit;
        this.expireTime = expireTime;
        this.desc = desc;
    }
}
