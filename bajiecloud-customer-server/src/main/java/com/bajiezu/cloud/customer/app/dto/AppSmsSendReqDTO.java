package com.bajiezu.cloud.customer.app.dto;

import com.bajiezu.cloud.customer.app.enums.AppSmsSceneEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AppSmsSendReqDTO {

    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    @Schema(description = "短信场景", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "短信场景不能为空")
    private AppSmsSceneEnum scene;
}
