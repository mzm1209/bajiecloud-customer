package com.bajiezu.cloud.customer.service;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.customervo.*;
import com.bajiezu.cloud.customer.dal.dto.CustomerListDto;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.entity.CustomerLabelInfo;
import com.bajiezu.cloud.customer.dal.entity.CustomerLog;
import com.bajiezu.cloud.customer.dal.entity.LabelInfo;
import com.bajiezu.cloud.customer.dal.mapper.*;
import com.bajiezu.cloud.customer.enums.OperateTypeEnum;
import com.bajiezu.cloud.customer.utils.MobileUtils;
import com.bajiezu.cloud.framework.security.LoginUser;
import com.bajiezu.cloud.framework.security.util.SecurityFrameworkUtils;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.CUSTOMER_NOT_EXIST;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService{

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerLogMapper customerLogMapper;

    @Autowired
    private CustomerExtMapper customerExtMapper;

    @Autowired
    private CustomerAddressMapper customerAddressMapper;

    @Autowired
    private CustomerLabelInfoMapper customerLabelInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private MergeLogMapper mergeLogMapper;

    @Resource
    private CustomerCacheService customerCacheService;

    @Override
    public PageResult<CustomerRespVO> list(CustomerListReqVO reqVO) {
        log.info("list req: {}", reqVO);
        LoginUser<?> loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw exception(LOGIN_EXCEPTION);
        }

        Integer offset = (reqVO.getPageNo() - 1) * reqVO.getPageSize();
        Integer limit = reqVO.getPageSize();

        CustomerListDto dto = new CustomerListDto();
        BeanUtils.copyProperties(reqVO, dto);
        dto.setOffset(offset);
        dto.setLimit(limit);
        log.info("list query dto: {}", dto);
        List<Customer> customers = customerMapper.queryListBy(dto);
        if (CollectionUtils.isEmpty(customers)) {
            return PageResult.empty();
        }
        Long count = customerMapper.queryCountBy(dto);
        log.info("list query get count: {}", count);

        List<Long> customerIds = customers.stream().map(Customer::getId).toList();
        // todo 根据 customerIds 查询用户 累计积分
        // todo 根据 customerIds 查询用户 成长值
        // todo 根据 customerIds 查询用户 下单次数


        List<CustomerRespVO> respVOList = Lists.newArrayList();
        for (Customer customer : customers) {
            CustomerRespVO respVO = buildCustomerRespVO(customer);
            respVOList.add(respVO);
        }
        return new PageResult<>(respVOList, count);
    }


    private CustomerRespVO buildCustomerRespVO(Customer customer) {
        CustomerRespVO respVO = new CustomerRespVO();
        respVO.setCustomerId(customer.getId());
        respVO.setThirdPartyId(customer.getThirdPartyId());
        respVO.setName(customer.getNickname());
        respVO.setMobile(MobileUtils.encryptMobile(customer.getMobile()));
        respVO.setEmail(customer.getEmail());
        respVO.setMemberLevel(customer.getMemberLevel());
        respVO.setSourceChannel(customer.getSourceChannel());
        respVO.setPlatformName(customer.getPlatformName());
        respVO.setIsBlackList(customer.getInBlackList());
        respVO.setRegisterTime(customer.getCreateTime());
        respVO.setLastOrderTime(customer.getLastOrderTime());
        // todo 根据条件查询之后设置其他参数

        return respVO;
    }

    @Override
    public CustomerDetailRespVO detail(CustomerBaseReqVO reqVO) {
        log.info("detail req: {}", reqVO);
        reqVO.validateParam();

        // 客户基础信息
        CustomerBaseDetail baseDetail = customerCacheService.getBaseInfo(reqVO.getCustomerId());
        if (baseDetail == null) {
            log.error("isBlack CUSTOMER_NOT_EXIST");
            throw exception(CUSTOMER_NOT_EXIST);
        }
        // 获取客户关联标签信息
        List<CustomerLabelRespVO> labelList = getLabel(reqVO);

        // todo 获取客户附件或者扩展信息

        CustomerDetailRespVO detailRespVO = new CustomerDetailRespVO();
        detailRespVO.setBaseDetail(baseDetail);
        detailRespVO.setLabelList(labelList);

        return detailRespVO;
    }

    @Override
    public void isBlack(CustomerBlackReqVO reqVO) {
        log.info("isBlack req: {}", reqVO);
        reqVO.validateParam();
        LoginUser<?> loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw exception(LOGIN_EXCEPTION);
        }
        List<Customer> customers = customerMapper.queryByIds(reqVO.getCustomerIds());
        if (CollectionUtils.isEmpty(customers)) {
            log.error("isBlack CUSTOMER_NOT_EXIST");
            throw exception(CUSTOMER_NOT_EXIST);
        }
        List<Long> customerIds = customers.stream().map(Customer::getId).toList();
        log.info("isBlack customerIds: {}", customerIds);

        customerMapper.updateByBlack(reqVO.getIsBlack(), reqVO.getBlackReason(), customerIds, loginUser.getId(), new Date());
        customerCacheService.batchClearCache(customerIds);

        List<CustomerLog> customerLogs = Lists.newArrayList();
        for (Long id : customerIds) {
            String operateType = reqVO.getIsBlack() == 1 ? OperateTypeEnum.IS_BLACK.getCode() : OperateTypeEnum.UN_BLACK.getCode();
            CustomerLog cLog = buildCustomerLog(id, operateType, reqVO.getBlackReason(), loginUser.getId());
            customerLogs.add(cLog);
        }
        customerLogMapper.batchInsert(customerLogs);
    }

    private CustomerLog buildCustomerLog(Long customerId, String operateType, String operateReason, Long operatorId) {
        CustomerLog customerLog = new CustomerLog();
        customerLog.setCustomerId(customerId);
        customerLog.setOperateType(operateType);
        customerLog.setOperatorId(operatorId);
        customerLog.setActionDesc(operateReason);
        customerLog.setCreateBy(operatorId);
        customerLog.setUpdatedBy(operatorId);
        customerLog.setCreateTime(new Date());
        customerLog.setUpdateTime(new Date());
        customerLog.setIsDeleted(0);
        return customerLog;
    }

    @Override
    public List<CustomerLabelRespVO> getLabel(CustomerBaseReqVO reqVO) {
        log.info("getLabel req: {}", reqVO);
        reqVO.validateParam();
        LoginUser<?> loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw exception(LOGIN_EXCEPTION);
        }
        List<CustomerLabelInfo> customerLabelInfos = customerLabelInfoMapper.queryListByCustomerId(reqVO.getCustomerId());
        if (CollectionUtils.isEmpty(customerLabelInfos)) {
            return Collections.emptyList();
        }
        List<Long> labelIds = customerLabelInfos.stream().map(CustomerLabelInfo::getLabelId).toList();
        log.info("getLabel labelIds: {}", labelIds);

        List<LabelInfo> labelInfos = labelInfoMapper.queryByIds(labelIds);
        return labelInfos.stream().map(labelInfo -> {
            CustomerLabelRespVO respVO = new CustomerLabelRespVO();
            respVO.setLabelId(labelInfo.getId());
            respVO.setName(labelInfo.getName());
            respVO.setLabelType(labelInfo.getLabelType());
            respVO.setRemark(labelInfo.getDescription());
            respVO.setStatus(labelInfo.getLabelStatus());
            respVO.setCreateTime(labelInfo.getCreateTime());
            return respVO;
        }).toList();
    }

    @Override
    public void addLabel(CustomerLabelAddVO addVO) {
        log.info("addLabel addVO: {}", addVO);
        addVO.validateParam();
        LoginUser<?> loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw exception(LOGIN_EXCEPTION);
        }
        Customer customer = customerMapper.selectById(addVO.getCustomerId());
        if (customer == null) {
            log.error("addLabel CUSTOMER_NOT_EXIST");
            throw exception(CUSTOMER_NOT_EXIST);
        }
        List<Long> addLabelIds = Lists.newArrayList();
        List<Long> delLabelIds = Lists.newArrayList();

        List<CustomerLabelInfo> customerLabelInfos = customerLabelInfoMapper.queryListByCustomerId(addVO.getCustomerId());
        if (CollectionUtils.isEmpty(customerLabelInfos)) {
            addLabelIds = addVO.getLabelIds();
        }else {
            List<Long> existLabelIds = customerLabelInfos.stream().map(CustomerLabelInfo::getLabelId).toList();
            addLabelIds = addVO.getLabelIds().stream().filter(labelId -> !existLabelIds.contains(labelId)).toList();
            delLabelIds = existLabelIds.stream().filter(labelId -> !addVO.getLabelIds().contains(labelId)).toList();
        }

        log.info("addLabel get addLabelIds: {}, delLabelIds: {}", addLabelIds, delLabelIds);
        if (CollectionUtils.isNotEmpty(addLabelIds)) {
            List<CustomerLabelInfo> addLabels = Lists.newArrayList();
            for (Long labelId : addLabelIds) {
                CustomerLabelInfo customerLabelInfo = buildCustomerLabelInfo(addVO.getCustomerId(), labelId, loginUser.getId());
                addLabels.add(customerLabelInfo);
            }
            customerLabelInfoMapper.batchInsert(addLabels);
        }
        if (CollectionUtils.isNotEmpty(delLabelIds)) {
            customerLabelInfoMapper.delCustomerLabelIds(addVO.getCustomerId(), delLabelIds, loginUser.getId(), new Date());
        }

        CustomerLog customerLog = buildCustomerLog(addVO.getCustomerId(), OperateTypeEnum.ADD_LABEL.getCode(), "", loginUser.getId());
        customerLogMapper.insert(customerLog);
    }

    private CustomerLabelInfo buildCustomerLabelInfo(Long customerId, Long labelId, Long operatorId) {
        CustomerLabelInfo customerLabelInfo = new CustomerLabelInfo();
        customerLabelInfo.setCustomerId(customerId);
        customerLabelInfo.setLabelId(labelId);
        customerLabelInfo.setCreateBy(operatorId);
        customerLabelInfo.setUpdatedBy(operatorId);
        customerLabelInfo.setCreateTime(new Date());
        customerLabelInfo.setUpdateTime(new Date());
        customerLabelInfo.setIsDeleted(0);
        return customerLabelInfo;
    }


    @Override
    public CustomerMemberLevelVO checkIsMember(CustomerBaseReqVO reqVO) {
        log.info("checkIsMember req: {}", reqVO);
        reqVO.validateParam();

        CustomerBaseDetail baseDetail = customerCacheService.getBaseInfo(reqVO.getCustomerId());
        if (baseDetail == null) {
            log.error("isBlack CUSTOMER_NOT_EXIST");
            throw exception(CUSTOMER_NOT_EXIST);
        }
        CustomerMemberLevelVO levelVO = new CustomerMemberLevelVO();
        levelVO.setCustomerId(baseDetail.getCustomerId());
        levelVO.setMemberLevel(baseDetail.getMemberLevel());
        return levelVO;
    }

    @Override
    public void updateMemberLevel(CustomerMemberLevelVO reqVO) {
        log.info("updateMemberLevel reqVO: {}", reqVO);
        reqVO.validateParam();
        LoginUser<?> loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw exception(LOGIN_EXCEPTION);
        }
        Customer customer = customerMapper.selectById(reqVO.getCustomerId());
        customer.setMemberLevel(reqVO.getMemberLevel());
        customer.setUpdatedBy(loginUser.getId());
        customer.setUpdateTime(new Date());
        customerMapper.updateById(customer);

        customerCacheService.clearCache(reqVO.getCustomerId());
    }

    @Override
    public void merge() {

    }

    @Override
    public void orderList(CustomerBaseReqVO reqVO) {

    }

    @Override
    public void amountInfo(CustomerBaseReqVO reqVO) {

    }

    @Override
    public void couponInfos(CustomerBaseReqVO reqVO) {

    }

    @Override
    public void addressInfo(CustomerBaseReqVO reqVO) {

    }

    @Override
    public void pointInfoList(CustomerBaseReqVO reqVO) {

    }

    @Override
    public void growthInfoList(CustomerBaseReqVO reqVO) {

    }
}
