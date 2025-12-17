package com.bajiezu.cloud.customer.controller.customervo;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "管理后台 - 用户中心 - 用户列表VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerListReqVO {

    @Schema(description = "手机号，精确搜索", example = "1")
    private String mobile;

    @Schema(description = "姓名", example = "xxx")
    private String name;

    @Schema(description = "用户ID", example = "1")
    private Long customerId;

    @Schema(description = "会员等级", example = "1")
    private Integer memberLevel;

    @Schema(description = "注册来源", example = "1")
    private String source;

    @Schema(description = "是否是黑名单用户 0:否 1:是", example = "1")
    private Integer isBlackCustomer;
}
