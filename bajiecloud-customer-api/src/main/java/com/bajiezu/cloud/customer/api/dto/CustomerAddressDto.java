package com.bajiezu.cloud.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "客户收货地址 DTO")
public class CustomerAddressDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "地址ID", example = "1")
    private Long id;

    @Schema(description = "客户ID", example = "1")
    private Long customerId;

    @Schema(description = "收货人姓名", example = "张三")
    private String receiverName;

    @Schema(description = "收货人手机号（已解密）", example = "13800000000")
    private String receiverMobile;

    @Schema(description = "省编码", example = "110000")
    private String provinceCode;

    @Schema(description = "省名称", example = "北京市")
    private String provinceName;

    @Schema(description = "市编码", example = "110100")
    private String cityCode;

    @Schema(description = "市名称", example = "北京市")
    private String cityName;

    @Schema(description = "区县编码（区划代码，对应 customer_address.area_code）", example = "110108")
    private String areaCode;

    @Schema(description = "区县名称", example = "海淀区")
    private String areaName;

    @Schema(description = "详细街道地址", example = "中关村大街 1 号")
    private String streetAddress;

    @Schema(description = "完整地址快照", example = "北京市北京市海淀区中关村大街 1 号")
    private String fullAddress;

    @Schema(description = "邮政编码", example = "100080")
    private String postalCode;

    @Schema(description = "地址类型：1-家，2-公司，3-学校，4-父母，5-朋友，9-其他/自定义", example = "1")
    private Integer addressType;

    @Schema(description = "地址标签", example = "家")
    private String addressTag;

    @Schema(description = "是否默认地址：0-否，1-是", example = "1")
    private Integer isDefault;
}
