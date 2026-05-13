package com.bajiezu.cloud.customer.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AppIdCardUploadRespVO {

    @Schema(description = "文件ID")
    private String fileId;

    @Schema(description = "文件类型，FRONT/BACK")
    private String fileType;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description = "上传状态")
    private String uploadStatus;
}
