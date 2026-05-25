package com.bajiezu.cloud.customer.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.api.dto.CustomerAddressDto;
import com.bajiezu.cloud.customer.api.dto.CustomerAddressQueryDto;
import com.bajiezu.cloud.customer.dal.entity.CustomerAddress;
import com.bajiezu.cloud.customer.dal.mapper.CustomerAddressMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.ADDRESS_NOT_EXIST;

@RestController
@Validated
public class CustomerAddressApiImpl implements CustomerAddressApi {

    @Value("${app.security.aes-key:}")
    private String aesKey;

    @Resource
    private CustomerAddressMapper customerAddressMapper;

    @Override
    public CommonResult<CustomerAddressDto> getAddressById(CustomerAddressQueryDto dto) {
        if (dto == null || dto.getId() == null) {
            throw exception(ADDRESS_NOT_EXIST);
        }
        LambdaQueryWrapper<CustomerAddress> wrapper = new LambdaQueryWrapper<CustomerAddress>()
                .eq(CustomerAddress::getId, dto.getId())
                .eq(CustomerAddress::getIsDeleted, 0);
        if (dto.getCustomerId() != null) {
            wrapper.eq(CustomerAddress::getCustomerId, dto.getCustomerId());
        }
        wrapper.last("limit 1");
        CustomerAddress address = customerAddressMapper.selectOne(wrapper);
        if (address == null) {
            throw exception(ADDRESS_NOT_EXIST);
        }
        return CommonResult.success(buildAddressDto(address));
    }

    private CustomerAddressDto buildAddressDto(CustomerAddress address) {
        CustomerAddressDto target = new CustomerAddressDto();
        target.setId(address.getId());
        target.setCustomerId(address.getCustomerId());
        target.setReceiverName(address.getReceiverName());
        target.setReceiverMobile(decryptIfPresent(address.getReceiverMobile()));
        target.setProvinceCode(address.getProvinceCode());
        target.setProvinceName(address.getProvinceName());
        target.setCityCode(address.getCityCode());
        target.setCityName(address.getCityName());
        target.setAreaCode(address.getAreaCode());
        target.setAreaName(address.getAreaName());
        target.setStreetAddress(address.getStreetAddress());
        target.setFullAddress(address.getFullAddress());
        target.setPostalCode(address.getPostalCode());
        target.setAddressType(address.getAddressType());
        target.setAddressTag(address.getAddressTag());
        target.setIsDefault(address.getIsDefault());
        return target;
    }

    private String decryptIfPresent(String encrypted) {
        if (StrUtil.hasBlank(encrypted, aesKey)) {
            return encrypted;
        }
        return SecureUtil.aes(aesKey.getBytes()).decryptStr(encrypted);
    }
}
