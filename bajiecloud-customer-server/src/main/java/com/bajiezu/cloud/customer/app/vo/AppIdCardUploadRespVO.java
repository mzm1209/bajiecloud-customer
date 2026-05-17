package com.bajiezu.cloud.customer.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppIdCardUploadRespVO {

    @Schema(description = "文件记录ID")
    private Long fileId;

    @Schema(description = "身份证面，FRONT/BACK")
    private String side;

    @Schema(description = "短时签名预览地址")
    private String previewUrl;

    @Schema(description = "OCR结果")
    private Object ocrResult;
}
