package com.bajiezu.cloud.customer.app.service;

import com.bajiezu.cloud.customer.app.dto.AddressCreateRequest;
import com.bajiezu.cloud.customer.app.dto.AddressDeleteRequest;
import com.bajiezu.cloud.customer.app.dto.AddressSetDefaultRequest;
import com.bajiezu.cloud.customer.app.dto.AddressUpdateRequest;
import com.bajiezu.cloud.customer.app.vo.AppCustomerProfileRespVO;
import com.bajiezu.cloud.customer.app.vo.AddressDetailVO;
import com.bajiezu.cloud.customer.app.vo.AddressListVO;

import java.util.List;
import java.util.Map;

public interface AppCustomerService {

    AppCustomerProfileRespVO getProfile();

    List<AddressListVO> getAddressList();

    AddressDetailVO getAddressDetail(Long id);

    Map<String, Long> createAddress(AddressCreateRequest req);

    Boolean updateAddress(AddressUpdateRequest req);

    Boolean deleteAddress(AddressDeleteRequest req);

    Boolean setDefaultAddress(AddressSetDefaultRequest req);
}
