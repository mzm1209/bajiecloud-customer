package com.bajiezu.cloud.customer.enums;

import java.util.concurrent.TimeUnit;

public interface IRedisKey {

    String getKey();

    TimeUnit getTimeUnit();

    long getExpireTime();

    String getDesc();

    default String format(Object... args) {
        return String.format(getKey(), args);
    }

    default String prefixedKey(String prefix) {
        return prefix + getKey();
    }

    default String prefixedFormat(String prefix, Object... args) {
        return prefix + String.format(getKey(), args);
    }
}
