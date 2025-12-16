package com.bajiezu.cloud.customer.dal.mapper;

import com.bajiezu.cloud.customer.dal.entity.CustomerLabelInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface CustomerLabelInfoMapper extends BaseMapper<CustomerLabelInfo> {

    List<CustomerLabelInfo> queryListByLabelIds(@Param("list") List<Long> labelIds);
}
