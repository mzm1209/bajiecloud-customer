package com.bajiezu.cloud.customer.app.vo;

import lombok.Data;

@Data
public class AppRealnameSubmitRespVO {
    private Integer realnameStatus;
    private Integer faceAuthStatus;
    private Integer authStatus;
    private String statusDesc;
    private String authOrderNo;
    private String realName;
    private String idCard;
    private String failReason;
}
