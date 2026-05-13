package com.bajiezu.cloud.customer.app.service.impl;

import com.bajiezu.cloud.customer.app.dto.AppAlipayLoginReqDTO;
import com.bajiezu.cloud.customer.app.dto.AppMobileLoginReqDTO;
import com.bajiezu.cloud.customer.app.service.AppAuthService;
import com.bajiezu.cloud.customer.app.vo.AppLoginRespVO;

public class AppAuthServiceImpl implements AppAuthService {

    @Override
    public AppLoginRespVO alipayLogin(AppAlipayLoginReqDTO reqDTO) {
        return new AppLoginRespVO();
    }

    @Override
    public AppLoginRespVO mobileLogin(AppMobileLoginReqDTO reqDTO) {
        return new AppLoginRespVO();
    }
}
