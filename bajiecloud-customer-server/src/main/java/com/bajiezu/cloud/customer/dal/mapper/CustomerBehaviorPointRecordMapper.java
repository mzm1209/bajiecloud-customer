package com.bajiezu.cloud.customer.dal.mapper;

import com.bajiezu.cloud.customer.dal.entity.CustomerAddress;
import com.bajiezu.cloud.customer.dal.entity.CustomerBehaviorPointRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerBehaviorPointRecordMapper extends BaseMapper<CustomerBehaviorPointRecord> {

    List<CustomerBehaviorPointRecord> queryByCustomerIdAndRuleId(@Param("customerId") Long customerId, @Param("taskId") Long taskId, @Param("ruleId") Long ruleId);
}
