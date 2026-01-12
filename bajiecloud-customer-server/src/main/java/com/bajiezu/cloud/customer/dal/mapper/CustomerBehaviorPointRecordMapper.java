package com.bajiezu.cloud.customer.dal.mapper;

import com.bajiezu.cloud.customer.dal.entity.CustomerAddress;
import com.bajiezu.cloud.customer.dal.entity.CustomerBehaviorPointRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CustomerBehaviorPointRecordMapper extends BaseMapper<CustomerBehaviorPointRecord> {

    List<CustomerBehaviorPointRecord> queryByCustomerIdAndRuleId(@Param("customerId") Integer customerId, @Param("taskId") Long taskId, @Param("ruleId") Integer ruleId);

    List<CustomerBehaviorPointRecord> queryAllBehaviorList(@Param("customerId") Long customerId);

    List<CustomerBehaviorPointRecord> queryList(@Param("customerId") Long customerId, @Param("offset") Integer offset, @Param("limit") Integer limit);

    Long queryCount(@Param("customerId") Long customerId);
}
