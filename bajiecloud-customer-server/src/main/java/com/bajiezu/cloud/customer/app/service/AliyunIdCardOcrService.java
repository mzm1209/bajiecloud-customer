package com.bajiezu.cloud.customer.app.service;

import com.bajiezu.cloud.customer.app.dto.ocr.IdCardOcrResultDTO;

public interface AliyunIdCardOcrService {

    IdCardOcrResultDTO recognizeByUrl(String imageUrl, String side);

    IdCardOcrResultDTO recognizeByFileKey(String fileKey, String side);
}
