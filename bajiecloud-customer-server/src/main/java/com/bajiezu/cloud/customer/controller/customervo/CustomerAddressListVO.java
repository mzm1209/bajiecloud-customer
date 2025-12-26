package com.bajiezu.cloud.customer.controller.customervo;

import com.bajiezu.cloud.common.web.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "管理后台 - 用户中心 - 地址列表")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomerAddressListVO extends PageParam {

    @Schema(description = "客户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotBlank(message = "客户ID不能为空")
    private Long customerId;

}
