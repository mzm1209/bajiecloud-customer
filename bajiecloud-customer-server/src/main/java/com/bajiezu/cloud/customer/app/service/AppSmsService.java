package com.bajiezu.cloud.customer.app.service;

import com.bajiezu.cloud.customer.app.dto.AppSmsSendReqDTO;
import com.bajiezu.cloud.customer.app.vo.AppSmsSendRespVO;

public interface AppSmsService {
    AppSmsSendRespVO sendLoginSms(AppSmsSendReqDTO reqDTO, String requestIp);
}
