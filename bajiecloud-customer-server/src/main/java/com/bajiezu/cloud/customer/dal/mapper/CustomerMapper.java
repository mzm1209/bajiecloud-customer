package com.bajiezu.cloud.customer.dal.mapper;

import com.bajiezu.cloud.customer.dal.dto.CustomerListDto;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {

    List<Customer> queryByIds(@Param("customerIds") List<Long> ids);

    void updateByBlack(@Param("isBlack") Integer isBlack, @Param("blackReason") String blackReason, @Param("customerIds") List<Long> ids,
                       @Param("updateBy") Long updateBy, @Param("updateTime") Date updateTime);


    List<Customer> queryListBy(CustomerListDto dto);

    Long queryCountBy(CustomerListDto dto);


    List<Customer> queryListByMobile(@Param("mobile") String mobile, @Param("offset") Integer offset, @Param("limit") Integer limit);

    Long queryCountByMobile(@Param("mobile") String mobile);

    void updateOrderCountAndLastOrderTime(@Param("customerId") Long customerId);
}
