package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.customervo.CustomerDetailRespVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerRespVO;
import com.bajiezu.cloud.customer.controller.labelvo.LabelRespVO;

import java.util.List;

public interface CustomerService {

    /**
     *  客户列表
     * */
    PageResult<CustomerRespVO> list();

    /**
     *  客户详情
     * */
    CustomerDetailRespVO detail();

    /**
     *  拉黑 / 解除拉黑
     * */
    void isBlack();

    /**
     *  打标签
     * */
    void addLabel();

    /**
     *  用户基本信息维护
     *  /api/customer/update
     * */
    void modBaseInfo();

}
