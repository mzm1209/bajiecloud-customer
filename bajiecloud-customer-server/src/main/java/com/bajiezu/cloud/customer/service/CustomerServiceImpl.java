package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.customervo.CustomerDetailRespVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerRespVO;
import com.bajiezu.cloud.customer.dal.entity.CustomerLog;
import com.bajiezu.cloud.customer.dal.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public PageResult<CustomerRespVO> list() {
        return null;
    }

    @Override
    public CustomerDetailRespVO detail() {
        return null;
    }

    @Override
    public void isBlack() {

    }

    @Override
    public void addLabel() {

    }

    @Override
    public void modBaseInfo() {

    }
}
