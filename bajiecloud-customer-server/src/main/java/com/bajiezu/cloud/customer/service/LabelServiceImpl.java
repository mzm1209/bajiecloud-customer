package com.bajiezu.cloud.customer.service;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.labelvo.*;
import com.bajiezu.cloud.customer.dal.entity.CustomerLabelInfo;
import com.bajiezu.cloud.customer.dal.entity.LabelInfo;
import com.bajiezu.cloud.customer.dal.mapper.CustomerLabelInfoMapper;
import com.bajiezu.cloud.customer.dal.mapper.LabelInfoMapper;
import com.bajiezu.cloud.customer.enums.LabelStatusEnum;
import com.bajiezu.cloud.framework.security.LoginUser;
import com.bajiezu.cloud.framework.security.util.SecurityFrameworkUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.*;

@Slf4j
@Service
public class LabelServiceImpl implements LabelService{

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private CustomerLabelInfoMapper customerLabelInfoMapper;

    @Override
    public void add(LabelAddReqVO reqVO) {
        log.info("add req: {}", reqVO);
        reqVO.validaParam();
        LoginUser<?> loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw exception(LOGIN_EXCEPTION);
        }
        LabelInfo info = labelInfoMapper.queryByName(reqVO.getName());
        if (info != null) {
            throw exception(NAME_EXIST);
        }
        info = buildInfo(reqVO, loginUser);
        labelInfoMapper.insert(info);
    }

    private LabelInfo buildInfo(LabelAddReqVO req, LoginUser user) {
        LabelInfo info = new LabelInfo();
        info.setName(req.getName());
        info.setLabelType(req.getLabelType());
        info.setDescription(req.getRemark());
        info.setLabelStatus(LabelStatusEnum.enable.getStatus());
        info.setCreateBy(user.getId());
        info.setUpdatedBy(user.getId());
        info.setCreateTime(new Date());
        info.setUpdateTime(new Date());
        info.setIsDeleted(0);
        return info;
    }

    @Override
    public void mod(LabelModReqVO reqVO) {
        log.info("edit req: {}", reqVO);
        reqVO.validateParam();
        LoginUser<?> loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw exception(LOGIN_EXCEPTION);
        }
        LabelInfo info = labelInfoMapper.selectById(reqVO.getId());
        if (info == null) {
            throw exception(LABEL_NO_EXIST);
        }
        if (!info.getName().equals(reqVO.getName())) {
            LabelInfo queryInfo = labelInfoMapper.queryByName(reqVO.getName());
            if (queryInfo != null) {
                throw exception(NAME_EXIST);
            }
        }
        info.setName(reqVO.getName());
        info.setDescription(reqVO.getRemark());
        info.setLabelType(reqVO.getLabelType());
        info.setUpdatedBy(loginUser.getId());
        info.setUpdateTime(new Date());
        labelInfoMapper.updateById(info);
    }

    @Override
    public void enable(LabelEnableReqVO reqVO) {
        log.info("enable req: {}", reqVO);
        reqVO.validateParam();
        LoginUser<?> loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw exception(LOGIN_EXCEPTION);
        }
        LabelInfo info = labelInfoMapper.selectById(reqVO.getId());
        if (info == null) {
            throw exception(LABEL_NO_EXIST);
        }
        info.setLabelStatus(reqVO.getStatus());
        info.setUpdatedBy(loginUser.getId());
        info.setUpdateTime(new Date());
        labelInfoMapper.updateById(info);
    }

    @Override
    public PageResult<LabelRespVO> list(LabelListReqVO reqVO) {
        log.info("list dto: {}", reqVO);
        Integer offset = (reqVO.getPageNo() - 1) * reqVO.getPageSize();
        Integer limit = reqVO.getPageSize();

        List<LabelInfo> list = labelInfoMapper.queryListBy(reqVO.getName(), reqVO.getStatus(), offset, limit);
        if (CollectionUtils.isEmpty(list)) {
            return PageResult.empty();
        }
        Long count = labelInfoMapper.queryCountBy(reqVO.getName(), reqVO.getStatus());
        log.info("list count : {}", count);

        List<Long> labelIds = list.stream().map(LabelInfo::getId).toList();
        Map<Long, List<CustomerLabelInfo>> customerInfoMap = null;
        if (CollectionUtils.isNotEmpty(labelIds)) {
            List<CustomerLabelInfo> customerInfoList = customerLabelInfoMapper.queryListByLabelIds(labelIds);
            if (CollectionUtils.isNotEmpty(customerInfoList)) {
                customerInfoMap = customerInfoList.stream().collect(Collectors.groupingBy(CustomerLabelInfo::getLabelId));
            }
        }
        Map<Long, List<CustomerLabelInfo>> finalCustomerInfoMap = customerInfoMap;
        List<LabelRespVO> respVOList = list.stream().map(info -> {
            LabelRespVO respVO = new LabelRespVO();
            respVO.setId(info.getId());
            respVO.setName(info.getName());
            respVO.setLabelType(info.getLabelType());
            respVO.setRemark(info.getDescription());
            respVO.setStatus(info.getLabelStatus());
            respVO.setCreateTime(info.getCreateTime());
            if (finalCustomerInfoMap != null && finalCustomerInfoMap.containsKey(info.getId())) {
                List<CustomerLabelInfo> customerLabelInfoList = finalCustomerInfoMap.get(info.getId());
                if (CollectionUtils.isNotEmpty(customerLabelInfoList)) {
                    respVO.setCustomerSize(customerLabelInfoList.size());
                }
            }
            return respVO;
        }).toList();
        return new PageResult<>(respVOList, count);
    }


    private static final String HEADER_TITLE = "标签管理列表";
    private static final String[] HEADERS = {"标签名称", "标签描述", "标签类型", "使用状态", "标签人数", "创建时间"};
    @Override
    public void export(LabelListReqVO reqVO, HttpServletResponse response) {

    }
}
