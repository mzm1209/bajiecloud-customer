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
     *  判断客户是否为会员
     *  0 普通成员
     *  大于0  会员 (在根据数据大小，区分会员等级)
     * */
    CustomerMemberLevelVO checkIsMember(CustomerBaseReqVO reqVO);

    /**
     *  变更客户为会员
     * */
    void updateMemberLevel(CustomerMemberLevelVO reqVO);


    /**
     *  客户合并
     * */
    void merge();

}
