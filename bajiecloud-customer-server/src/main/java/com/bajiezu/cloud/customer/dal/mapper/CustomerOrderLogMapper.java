package com.bajiezu.cloud.customer.dal.mapper;

import com.bajiezu.cloud.customer.dal.entity.CustomerOrderLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CustomerOrderLogMapper extends BaseMapper<CustomerOrderLog> {

    Integer queryCountByOrderNo(@Param("orderNo") String orderNo);

}
