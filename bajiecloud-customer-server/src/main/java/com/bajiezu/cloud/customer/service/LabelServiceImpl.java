package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.vo.*;
import com.bajiezu.cloud.customer.dal.mapper.CustomerLabelInfoMapper;
import com.bajiezu.cloud.customer.dal.mapper.LabelInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LabelServiceImpl implements LabelService{

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private CustomerLabelInfoMapper customerLabelInfoMapper;

    @Override
    public void add(LabelAddReqVO reqVO) {

    }

    @Override
    public void edit(LabelModReqVO reqVO) {

    }

    @Override
    public void enabel(LabelEnableReqVO reqVO) {

    }

    @Override
    public PageResult<LabelRespVO> list(LabelListReqVO reqVO) {
        return null;
    }

    @Override
    public void export() {

    }
}
