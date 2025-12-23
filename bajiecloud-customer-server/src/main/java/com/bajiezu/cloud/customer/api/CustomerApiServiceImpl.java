package com.bajiezu.cloud.customer.api;

import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.api.dto.*;
import com.bajiezu.cloud.customer.controller.customervo.*;
import com.bajiezu.cloud.customer.service.CustomerCacheService;
import com.bajiezu.cloud.customer.service.CustomerService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class CustomerApiServiceImpl implements CustomerApiService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerCacheService customerCacheService;
    @Override
    public CommonResult<Boolean> isBlack(CustomerBlackDto dto) {
        CustomerBlackReqVO reqVO = new CustomerBlackReqVO();
        BeanUtils.copyProperties(dto, reqVO);
        customerService.isBlack(reqVO);
        return CommonResult.success(true);
    }

    @Override
    public CommonResult<Boolean> addLabel(CustomerLabelAddDto dto) {
        CustomerLabelAddVO addVO = new CustomerLabelAddVO();
        BeanUtils.copyProperties(dto, addVO);
        addVO.setCustomerId(dto.getCustomerId());
        customerService.addLabel(addVO);
        return CommonResult.success(true);
    }

    @Override
    public CommonResult<CustomerBaseDetailInfo> getBaseInfo(CustomerBaseDto dto) {
        CustomerBaseDetail baseDetail = customerCacheService.getBaseInfo(dto.getCustomerId());
        CustomerBaseDetailInfo detailInfo = new CustomerBaseDetailInfo();
        BeanUtils.copyProperties(baseDetail, detailInfo);
        return CommonResult.success(detailInfo);
    }

    @Override
    public CommonResult<CustomerMemberLevelDto> checkIsMember(CustomerBaseDto dto) {
        CustomerBaseReqVO reqVO = new CustomerBaseReqVO();
        reqVO.setCustomerId(dto.getCustomerId());
        CustomerMemberLevelVO levelVO = customerService.checkIsMember(reqVO);
        CustomerMemberLevelDto levelDto = new CustomerMemberLevelDto();
        BeanUtils.copyProperties(levelVO, levelDto);
        return CommonResult.success(levelDto);
    }

    @Override
    public CommonResult<Boolean> updateMemberLevel(CustomerMemberLevelDto dto) {
        CustomerMemberLevelVO levelVO = new CustomerMemberLevelVO();
        BeanUtils.copyProperties(dto, levelVO);
        customerService.updateMemberLevel(levelVO);
        return CommonResult.success(true);
    }
}
