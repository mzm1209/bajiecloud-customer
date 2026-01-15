package com.bajiezu.cloud.customer.api.dto;

import com.bajiezu.cloud.common.web.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CustomerMobileDto extends PageParam {

    @Schema(description = "手机号，前缀匹配搜索", example = "1")
    private String mobile;
}
