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
    private Integer gender;
    private String birthday;
    private String ethnicity;
    private String address;
    private String issueAuthority;
    private String validStart;
    private String validEnd;
    private String mobile;
    private String email;
    private String failReason;
}
