package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.customervo.*;

import java.util.List;

public interface CustomerService {

    void mockAddCustomer();

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
     * 添加地址
     * */
    void addAddress(CustomerAddressVO reqVO);

    /**
     *  客户地址信息列表
     * */
    PageResult<CustomerAddressVO> addressInfoList(CustomerAddressListVO reqVO);


    /**
     *  根据手机号查询客户信息
     * */
    PageResult<CustomerInfoRespVO> mobileList(MobileReqVO reqVO);


    /**
     *  客户合并
     * */
    void merge();

}
