package com.bajiezu.cloud.customer.app.vo;

import lombok.Data;

@Data
public class AppLoginRespVO {
    private String tokenName;
    private String token;
    private Integer expireTime;
    private Long customerId;
    private Boolean hasMobile;
    private String mobile;
    private Integer realnameStatus;
    private Integer faceAuthStatus;
    private Integer accountStatus;
}
