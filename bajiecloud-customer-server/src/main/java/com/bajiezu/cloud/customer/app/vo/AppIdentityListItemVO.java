package com.bajiezu.cloud.customer.app.vo;

import lombok.Data;

@Data
public class AppIdentityListItemVO {
    private Long id;
    private Integer realnameStatus;
    private Integer authStatus;
    private String statusDesc;
    private String submitTime;
    private String passTime;
    private String realName;
    private String idCard;
    private String failReason;
}
