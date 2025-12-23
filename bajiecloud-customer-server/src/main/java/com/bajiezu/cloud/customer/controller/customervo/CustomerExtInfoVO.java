package com.bajiezu.cloud.customer.controller.customervo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerExtInfoVO {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "扩展类型：AlipayInfo, JDInfo, WechatInfo, BaseExt", example = "1")
    private String extType;

    @Schema(description = "扩展字段Key", example = "1")
    private String extKey;

    @Schema(description = "扩展字段value", example = "1")
    private String extValue;

    @Schema(description = "数据来源：System, Alipay, JD, Wechat, Manual", example = "1")
    private String sourceFrom;

    @Schema(description = "是否有效：0-否，1-是", example = "1")
    private Integer isValid;
}
