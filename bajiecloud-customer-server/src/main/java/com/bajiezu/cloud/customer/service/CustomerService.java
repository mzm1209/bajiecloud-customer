package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.customervo.CustomerAddressListVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerAddressVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerBaseReqVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerBlackReqVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerDetailRespVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerInfoRespVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerLabelAddVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerLabelRespVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerListReqVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerMemberLevelVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerRespVO;
import com.bajiezu.cloud.customer.controller.customervo.MobileReqVO;
import com.bajiezu.cloud.customer.controller.request.CustomerQueryRequest;
import com.bajiezu.cloud.customer.utils.Id2NameDto;

import java.util.Collection;
import java.util.List;

public interface CustomerService {

  void mockAddCustomer();

  /**
   * 客户列表
   */
  PageResult<CustomerRespVO> list(CustomerListReqVO reqVO);

  /**
   * 客户详情
   */
  CustomerDetailRespVO detail(CustomerBaseReqVO reqVO);

  /**
   * 拉黑 / 解除拉黑
   */
  void isBlack(CustomerBlackReqVO reqVO);

  /**
   * 查询用户身上标签
   */
  List<CustomerLabelRespVO> getLabel(CustomerBaseReqVO reqVO);

  /**
   * 给用户打标签
   */
  void addLabel(CustomerLabelAddVO addVO);

  /**
   * 判断客户是否为会员
   * 0 普通成员
   * 大于0  会员 (在根据数据大小，区分会员等级)
   */
  CustomerMemberLevelVO checkIsMember(CustomerBaseReqVO reqVO);

  /**
   * 变更客户为会员
   */
  void updateMemberLevel(CustomerMemberLevelVO reqVO);


  /**
   * 添加地址
   */
  void addAddress(CustomerAddressVO reqVO);

  /**
   * 客户地址信息列表
   */
  PageResult<CustomerAddressVO> addressInfoList(CustomerAddressListVO reqVO);


  /**
   * 根据手机号查询客户信息
   */
  PageResult<CustomerInfoRespVO> mobileList(MobileReqVO reqVO);

  PageResult<CustomerInfoRespVO> querySimpleCustomerInfoByKey(CustomerQueryRequest queryRequest);

  /**
   * 根据客户ID列表查询客户名称
   */
  List<Id2NameDto> queryCustomerNameByIds(Collection<Long> ids);

  /**
   * 客户合并
   */
  void merge();

}
