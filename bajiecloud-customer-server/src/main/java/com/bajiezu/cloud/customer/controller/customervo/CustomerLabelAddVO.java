package com.bajiezu.cloud.customer.controller.customervo;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.google.common.base.Preconditions;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Schema(description = "管理后台 - 用户中心 - 打标签VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomerLabelAddVO extends CustomerBaseReqVO{

    @Schema(description = "标签ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1]")
    @NotBlank(message = "标签ID不能为空")
    private List<Long> labelIds;

    public void validateParam() {
        super.validateParam();
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(labelIds), "标签ID不能为空");
    }
}
