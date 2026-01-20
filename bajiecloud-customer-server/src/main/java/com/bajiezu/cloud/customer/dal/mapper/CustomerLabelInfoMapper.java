package com.bajiezu.cloud.customer.dal.mapper;

import com.bajiezu.cloud.customer.dal.entity.CustomerLabelInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

@Mapper
public interface CustomerLabelInfoMapper extends BaseMapper<CustomerLabelInfo> {

    List<CustomerLabelInfo> queryListByLabelIds(@Param("list") List<Long> labelIds);

    List<CustomerLabelInfo> queryListByCustomerId(@Param("customerId") Long customerId);

    void batchInsert(@Param("list") List<CustomerLabelInfo> infos);

    void delCustomerLabelIds(@Param("customerId") Long customerId, @Param("labelIds") List<Long> labelIds);
}
