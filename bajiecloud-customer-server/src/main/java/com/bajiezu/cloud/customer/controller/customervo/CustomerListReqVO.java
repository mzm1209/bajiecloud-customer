package com.bajiezu.cloud.customer.controller.customervo;


import com.bajiezu.cloud.common.web.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Schema(description = "管理后台 - 用户中心 - 用户列表VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomerListReqVO extends PageParam {

    @Schema(description = "手机号，精确搜索", example = "1")
    private String mobile;

    @Schema(description = "姓名", example = "xxx")
    private String name;

    @Schema(description = "用户ID", example = "1")
    private Long customerId;

    @Schema(description = "会员等级", example = "1")
    private List<Integer> memberLevels;

    @Schema(description = "注册来源", example = "1")
    private List<String> platformNames;

    @Schema(description = "访问id列表", example = "1")
    private List<String> thirdPartyIds;

    @Schema(description = "是否是黑名单用户 0:否 1:是", example = "1")
    private Integer isBlackCustomer;
}
