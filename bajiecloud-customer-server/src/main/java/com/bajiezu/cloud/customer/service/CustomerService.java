package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.customervo.*;
import com.bajiezu.cloud.customer.controller.labelvo.LabelRespVO;

import java.util.List;

public interface CustomerService {

    /**
     *  客户列表
     * */
    PageResult<CustomerRespVO> list(CustomerListReqVO reqVO);

    /**
     *  客户详情
     * */
    CustomerDetailRespVO detail(CustomerBaseReqVO reqVO);

    /**
     *  拉黑 / 解除拉黑
     * */
    void isBlack(CustomerBlackReqVO reqVO);

    /**
     *  查询用户身上标签
     * */
    List<CustomerLabelRespVO> getLabel(CustomerBaseReqVO reqVO);

    /**
     *  给用户打标签
     * */
    void addLabel(CustomerLabelAddVO addVO);

    /**
     *  用户基本信息维护
     *  /api/customer/update
     * */
    CustomerBaseDetail getBaseInfo(CustomerBaseReqVO reqVO);

}
