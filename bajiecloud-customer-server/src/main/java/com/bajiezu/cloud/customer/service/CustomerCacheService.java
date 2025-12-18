package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.customer.controller.customervo.CustomerBaseDetail;
import com.bajiezu.cloud.customer.controller.customervo.CustomerBaseReqVO;

import java.util.List;

public interface CustomerCacheService {

    /**
     *  用户基本信息维护
     *  /api/customer/update
     * */
    CustomerBaseDetail getBaseInfo(Long customerId);


    void clearCache(Long customerId);


    void batchClearCache(List<Long> customerIds);
}
