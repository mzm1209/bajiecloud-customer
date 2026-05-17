package com.bajiezu.cloud.customer.domain.dto.ocr;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Locale;

@Data
public class IdCardOcrRequestDTO {

    private String fileKey;
    private String imageUrl;

    @NotBlank(message = "side不能为空")
    private String side;

    @AssertTrue(message = "imageUrl和fileKey至少一个不能为空")
    public boolean hasFileKeyOrImageUrl() {
        return StringUtils.hasText(fileKey) || StringUtils.hasText(imageUrl);
    }

    @AssertTrue(message = "side只允许FRONT/BACK")
    public boolean isValidSide() {
        if (!StringUtils.hasText(side)) {
            return false;
        }
        String normalized = side.trim().toUpperCase(Locale.ROOT);
        return "FRONT".equals(normalized) || "BACK".equals(normalized);
    }
}
