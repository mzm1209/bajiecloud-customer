package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.customer.domain.dto.ocr.IdCardOcrResultDTO;

public interface AliyunIdCardOcrService {

    IdCardOcrResultDTO recognizeByUrl(String imageUrl, String side);

    IdCardOcrResultDTO recognizeByFileKey(String fileKey, String side);
}
