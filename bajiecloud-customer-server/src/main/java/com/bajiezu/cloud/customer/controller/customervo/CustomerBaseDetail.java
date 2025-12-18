package com.bajiezu.cloud.customer.controller.customervo;

import lombok.Data;

import java.util.Date;

@Data
public class CustomerBaseDetail {

    private Long customerId;

    private String platformUid;

    private String thirdPartyId;

    private String platformName;

    private String sourceChannel;

    private String sourceLevel;

    private String sourcePoint;

    private String mobile;

    private String email;

    private String wechatId;

    private String wechatMobile;

    private String idCard;

    private String idCardHash;

    private String nickName;

    private String avatarUrl;

    private String realName;

    private Integer gender;

    private Date birthday;

    private String areaCode;

    private Integer memberLevel;

    private Integer isBlackList;

    private Integer isAnonymous;

    private Integer accountStatus;
}
