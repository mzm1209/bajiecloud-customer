package com.bajiezu.cloud.customer.app.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Data
public class AddressCreateRequest {
    @NotBlank(message = "receiverName不能为空")
    @Size(min = 2, max = 30, message = "receiverName长度需在2-30")
    private String receiverName;
    @NotBlank(message = "receiverMobile不能为空")
    @Pattern(regexp = "^1\\d{10}$", message = "receiverMobile格式不正确")
    private String receiverMobile;
    private String provinceCode;
    @NotBlank(message = "provinceName不能为空")
    private String provinceName;
    private String cityCode;
    @NotBlank(message = "cityName不能为空")
    private String cityName;
    @NotBlank(message = "areaCode不能为空")
    private String areaCode;
    @NotBlank(message = "areaName不能为空")
    private String areaName;
    @NotBlank(message = "streetAddress不能为空")
    @Size(min = 5, message = "streetAddress长度至少5")
    private String streetAddress;
    private String postalCode;
    @NotNull(message = "addressType不能为空")
    private Integer addressType;
    private String addressTag;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Boolean isDefault;
}
