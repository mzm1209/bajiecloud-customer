package com.bajiezu.cloud.customer.controller.customervo;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Schema(description = "管理后台 - 用户中心 - 地址管理VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAddressVO extends CustomerBaseReqVO {

    @Schema(description = "地址类型：1-家庭，2-公司，3-学校，4-其他", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer addressType;

    @Schema(description = "收货人姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "xx")
    @NotBlank(message = "收货人姓名不能为空")
    private String name;

    @Schema(description = "收货人手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "收货人手机号不能为空")
    private String mobile;

    @Schema(description = "选中的区县代码，关联地址表的code字段", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotBlank(message = "区县代码不能为空")
    private String areaCode;

    @Schema(description = "详细街道地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "xxx")
    @NotBlank(message = "详细街道地址不能为空")
    private String streetAddress;

    @Schema(description = "邮政编码", example = "xxx")
    private String postCode;

    @Schema(description = "是否默认地址：0-否，1-是", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "是否默认不能为空")
    private Integer isDefault;

    private Date createTime;
}
