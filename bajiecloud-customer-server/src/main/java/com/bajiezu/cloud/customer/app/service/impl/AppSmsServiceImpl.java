package com.bajiezu.cloud.customer.app.service.impl;

import com.bajiezu.cloud.customer.app.dto.AppSmsSendReqDTO;
import com.bajiezu.cloud.customer.app.service.AppSmsService;

public class AppSmsServiceImpl implements AppSmsService {

    @Override
    public Boolean sendLoginSms(AppSmsSendReqDTO reqDTO) {
        return Boolean.TRUE;
    }
}
