package com.bajiezu.cloud.customer.app.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Data
public class AddressUpdateRequest {
    @NotNull(message = "地址ID不能为空")
    private Long id;
    @NotBlank(message = "请填写收货人姓名")
    @Size(min = 2, max = 30, message = "收货人姓名需为2-30个字符")
    private String receiverName;
    @NotBlank(message = "请填写手机号码")
    @Pattern(regexp = "^1\\d{10}$", message = "请填写正确的手机号码")
    private String receiverMobile;
    private String provinceCode;
    @NotBlank(message = "请选择省份")
    private String provinceName;
    private String cityCode;
    @NotBlank(message = "请选择城市")
    private String cityName;
    @NotBlank(message = "请选择所在地区")
    private String areaCode;
    @NotBlank(message = "请选择所在地区")
    private String areaName;
    @NotBlank(message = "请填写详细地址")
    @Size(min = 5, message = "详细地址至少需要5个字符")
    private String streetAddress;
    private String postalCode;
    @NotNull(message = "请选择地址标签")
    private Integer addressType;
    private String addressTag;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Boolean isDefault;
}
