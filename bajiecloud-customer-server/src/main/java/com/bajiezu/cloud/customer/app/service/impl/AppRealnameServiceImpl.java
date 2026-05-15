package com.bajiezu.cloud.customer.app.service.impl;

import com.bajiezu.cloud.customer.app.dto.AppRealnameSubmitReqDTO;
import com.bajiezu.cloud.customer.app.service.AppRealnameService;
import com.bajiezu.cloud.customer.app.vo.AppFaceAuthResultRespVO;
import com.bajiezu.cloud.customer.app.vo.AppIdCardUploadRespVO;
import com.bajiezu.cloud.customer.app.vo.AppRealnameStatusRespVO;

public class AppRealnameServiceImpl implements AppRealnameService {

    @Override
    public Boolean submitRealname(AppRealnameSubmitReqDTO reqDTO) {
        return Boolean.TRUE;
    }

    @Override
    public AppRealnameStatusRespVO getRealnameStatus() {
        return new AppRealnameStatusRespVO();
    }

    @Override
    public AppIdCardUploadRespVO uploadIdCard() {
        return new AppIdCardUploadRespVO();
    }

    @Override
    public String initFaceAuth() {
        return "";
    }

    @Override
    public AppFaceAuthResultRespVO getFaceAuthResult() {
        return new AppFaceAuthResultRespVO();
    }
}
