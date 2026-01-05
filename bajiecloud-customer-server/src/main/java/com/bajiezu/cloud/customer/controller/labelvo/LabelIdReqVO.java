package com.bajiezu.cloud.customer.controller.labelvo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LabelIdReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;
}
