package com.bajiezu.cloud.customer.app.vo;

import lombok.Data;

@Data
public class AddressListVO {
    private Long id;
    private String receiverName;
    private String receiverMobile;
    private String provinceName;
    private String cityName;
    private String areaName;
    private String areaCode;
    private String streetAddress;
    private String fullAddress;
    private Integer addressType;
    private String addressTag;
    private Boolean isDefault;
}
