package com.bajiezu.cloud.customer.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppRealnameSubmitReqDTO {

    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @Schema(description = "身份证号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    @Schema(description = "身份证正面文件ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "身份证正面文件ID不能为空")
    private String idCardFrontFileId;

    @Schema(description = "身份证反面文件ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "身份证反面文件ID不能为空")
    private String idCardBackFileId;
}
