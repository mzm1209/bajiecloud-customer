package com.bajiezu.cloud.customer.api;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.api.dto.*;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerBehaviorVO;
import com.bajiezu.cloud.customer.controller.customervo.*;
import com.bajiezu.cloud.customer.service.CustomerBehaviorService;
import com.bajiezu.cloud.customer.service.CustomerCacheService;
import com.bajiezu.cloud.customer.service.CustomerService;
import com.bajiezu.cloud.customer.utils.Id2NameDto;
import com.google.common.collect.Lists;
import java.util.Collection;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Validated
public class CustomerApiImpl implements CustomerApi {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerCacheService customerCacheService;

    @Autowired
    private CustomerBehaviorService customerBehaviorService;

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

    @Override
    public CommonResult<Boolean> behaviorHandle(CustomerBehaviorDto dto) {
        CustomerBehaviorVO behaviorVO = new CustomerBehaviorVO();
        BeanUtils.copyProperties(dto, behaviorVO);
        customerBehaviorService.handleCustomerBehavior(behaviorVO);
        return CommonResult.success(true);
    }

    @Override
    public CommonResult<PageResult<CustomerInfoDto>> mobileList(CustomerMobileDto reqVO) {
        MobileReqVO mobileReqVO = new MobileReqVO();
        BeanUtils.copyProperties(reqVO, mobileReqVO);
        mobileReqVO.setPageNo(reqVO.getPageNo());
        mobileReqVO.setPageSize(reqVO.getPageSize());
        PageResult<CustomerInfoRespVO> result = customerService.mobileList(mobileReqVO);
        if (result == null) {
            return CommonResult.success(PageResult.empty());
        }
        Long count = result.getTotal();
        List<CustomerInfoDto> infoDtos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(result.getList())) {
            for (CustomerInfoRespVO respVO : result.getList()) {
                CustomerInfoDto infoDto = new CustomerInfoDto();
                BeanUtils.copyProperties(respVO, infoDto);
                infoDtos.add(infoDto);
            }
        }
        return CommonResult.success(new PageResult<>(infoDtos, count));
    }

    @Override
    public CommonResult<List<Id2NameDto>> queryCustomerNameByIds(Collection<Long> ids) {
        return CommonResult.success(customerService.queryCustomerNameByIds(ids));
    }
}
