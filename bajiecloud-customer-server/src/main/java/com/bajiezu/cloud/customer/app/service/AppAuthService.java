package com.bajiezu.cloud.customer.app.service;

import com.bajiezu.cloud.customer.app.dto.AppAlipayLoginReqDTO;
import com.bajiezu.cloud.customer.app.dto.AppMobileLoginReqDTO;
import com.bajiezu.cloud.customer.app.vo.AppLoginRespVO;

public interface AppAuthService {

    AppLoginRespVO alipayLogin(AppAlipayLoginReqDTO reqDTO);

    AppLoginRespVO mobileLogin(AppMobileLoginReqDTO reqDTO);

    Boolean logout(String token);
}
