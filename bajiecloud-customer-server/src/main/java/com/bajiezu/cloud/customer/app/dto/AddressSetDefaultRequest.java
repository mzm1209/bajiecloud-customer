package com.bajiezu.cloud.customer.app.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class AddressSetDefaultRequest {
    @NotNull(message = "id不能为空")
    private Long id;
}
