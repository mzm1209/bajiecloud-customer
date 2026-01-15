package com.bajiezu.cloud.customer.controller.customervo;

import com.bajiezu.cloud.common.web.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - 用户中心 - 根据手机号查询列表VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class MobileReqVO extends PageParam {

    @Schema(description = "手机号，前缀匹配搜索", example = "1")
    private String mobile;
}
