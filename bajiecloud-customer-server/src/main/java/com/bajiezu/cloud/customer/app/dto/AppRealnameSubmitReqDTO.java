package com.bajiezu.cloud.customer.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AppRealnameSubmitReqDTO {
    @Schema(description = "真实姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "真实姓名不能为空")
    private String realName;
    @Schema(description = "身份证号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "(^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$)|(^[1-9]\\d{7}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}$)", message = "身份证号有误")
    private String idCard;
    @NotNull(message = "身份证正面文件ID不能为空")
    private Long idCardFrontFileId;
    @NotNull(message = "身份证反面文件ID不能为空")
    private Long idCardBackFileId;
    private String validStart;
    private String validEnd;
    private Integer gender;
    private String birthday;
    private String ethnicity;
    private String address;
    private String issueAuthority;
    @Schema(description = "手机号")
    @Pattern(regexp = "^1\\d{10}$", message = "手机号格式不正确")
    private String mobile;
    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;
    @NotNull(message = "请同意实名认证协议")
    private Boolean agreeAuth;
}
