package com.bajiezu.cloud.customer.app.vo;

import lombok.Data;

@Data
public class AppIdentityDetailRespVO {
    private Long id;
    private Integer authStatus;
    private Integer realnameStatus;
    private String statusDesc;
    private String submitTime;
    private String passTime;
    private String failReason;
    private String realName;
    private String idCard;
    private String mobile;
    private String email;
    private Integer gender;
    private String birthday;
    private String ethnicity;
    private String address;
    private String issueAuthority;
    private String validStart;
    private String validEnd;
    private Long idCardFrontFileId;
    private Long idCardBackFileId;
    private String idCardFrontUrl;
    private String idCardBackUrl;
}
