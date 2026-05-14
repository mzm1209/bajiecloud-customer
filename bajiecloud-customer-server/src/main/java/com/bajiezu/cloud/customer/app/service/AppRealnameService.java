package com.bajiezu.cloud.customer.app.service;

import com.bajiezu.cloud.customer.app.dto.AppRealnameSubmitReqDTO;
import com.bajiezu.cloud.customer.app.vo.AppFaceAuthResultRespVO;
import com.bajiezu.cloud.customer.app.vo.AppIdCardUploadRespVO;
import com.bajiezu.cloud.customer.app.vo.AppRealnameStatusRespVO;

public interface AppRealnameService {

    Boolean submitRealname(AppRealnameSubmitReqDTO reqDTO);

    AppRealnameStatusRespVO getRealnameStatus();

    AppIdCardUploadRespVO uploadIdCard();

    String initFaceAuth();

    AppFaceAuthResultRespVO getFaceAuthResult();
}
