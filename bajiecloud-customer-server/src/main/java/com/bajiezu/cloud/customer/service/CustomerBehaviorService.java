package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerBehaviorListReqVO;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerBehaviorRespVO;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerBehaviorVO;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerTotalPointRespVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerBaseReqVO;

public interface CustomerBehaviorService {

    void handleCustomerBehavior(CustomerBehaviorVO vo);

    CustomerTotalPointRespVO customerTotalPoint(CustomerBaseReqVO reqVO);

    PageResult<CustomerBehaviorRespVO> list(CustomerBehaviorListReqVO reqVO);
}
