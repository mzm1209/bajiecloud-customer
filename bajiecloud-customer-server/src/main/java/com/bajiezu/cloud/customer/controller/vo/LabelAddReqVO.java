package com.bajiezu.cloud.customer.controller.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Schema(description = "管理后台 - 用户中心 - 标签管理 - 新增VO")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelAddReqVO extends LabelBaseVO{


}
