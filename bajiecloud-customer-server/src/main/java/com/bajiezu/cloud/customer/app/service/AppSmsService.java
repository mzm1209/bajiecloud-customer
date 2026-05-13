package com.bajiezu.cloud.customer.app.service;

import com.bajiezu.cloud.customer.app.dto.AppSmsSendReqDTO;

public interface AppSmsService {

    Boolean sendLoginSms(AppSmsSendReqDTO reqDTO);
}
