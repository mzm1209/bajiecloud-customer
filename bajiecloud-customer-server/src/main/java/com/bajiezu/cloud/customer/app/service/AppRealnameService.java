package com.bajiezu.cloud.customer.app.service;

import com.bajiezu.cloud.customer.app.vo.AppIdCardUploadRespVO;
import com.bajiezu.cloud.customer.domain.dto.ocr.IdCardOcrRequestDTO;
import com.bajiezu.cloud.customer.domain.dto.ocr.IdCardOcrResultDTO;
import org.springframework.web.multipart.MultipartFile;

public interface AppRealnameService {

    AppIdCardUploadRespVO uploadIdCard(String side, MultipartFile file);

    IdCardOcrResultDTO idCardOcr(IdCardOcrRequestDTO requestDTO);
}
