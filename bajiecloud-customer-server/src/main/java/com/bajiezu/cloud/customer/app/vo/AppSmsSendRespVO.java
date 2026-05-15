package com.bajiezu.cloud.customer.app.vo;

import lombok.Data;

@Data
public class AppSmsSendRespVO {
    private Integer cooldownSeconds;
    private Integer expireSeconds;
}
