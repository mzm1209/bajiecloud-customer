package com.bajiezu.cloud.customer.app.service;

import com.bajiezu.cloud.customer.app.vo.AppIdCardUploadRespVO;
import org.springframework.web.multipart.MultipartFile;

public interface AppRealnameService {

    AppIdCardUploadRespVO uploadIdCard(String side, MultipartFile file);
}
