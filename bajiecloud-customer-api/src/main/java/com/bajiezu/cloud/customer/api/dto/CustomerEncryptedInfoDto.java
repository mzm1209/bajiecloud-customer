package com.bajiezu.cloud.customer.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CustomerEncryptedInfoDto {

    @Schema(description = "真实姓名(密文)")
    private String realName;

    @Schema(description = "手机号(密文)")
    private String mobile;

    @Schema(description = "身份证号(密文)")
    private String idCard;
}
