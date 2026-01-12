package com.bajiezu.cloud.customer.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class Id2NameDto {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "name", example = "xx")
    private String name;
}
