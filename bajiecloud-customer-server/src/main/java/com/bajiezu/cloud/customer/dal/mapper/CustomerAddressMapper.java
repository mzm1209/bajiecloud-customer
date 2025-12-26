package com.bajiezu.cloud.customer.dal.mapper;

import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.entity.CustomerAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerAddressMapper extends BaseMapper<CustomerAddress> {

    List<CustomerAddress> queryList(@Param("customerId") Long customerId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    Long queryCount(@Param("customerId") Long customerId);

}
