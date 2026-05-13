package com.bajiezu.cloud.customer.app.service.impl;

import com.bajiezu.cloud.customer.app.service.AppCustomerService;
import com.bajiezu.cloud.customer.app.vo.AppCustomerProfileRespVO;

public class AppCustomerServiceImpl implements AppCustomerService {

    @Override
    public AppCustomerProfileRespVO getProfile() {
        return new AppCustomerProfileRespVO();
    }
}
