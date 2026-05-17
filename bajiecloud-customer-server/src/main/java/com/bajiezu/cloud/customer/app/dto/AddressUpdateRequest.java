package com.bajiezu.cloud.customer.app.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddressUpdateRequest {
    private Long id;
    private String receiverName;
    private String receiverMobile;
    private String provinceCode;
    private String provinceName;
    private String cityCode;
    private String cityName;
    private String areaCode;
    private String areaName;
    private String streetAddress;
    private String postalCode;
    private Integer addressType;
    private String addressTag;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer isDefault;
}
