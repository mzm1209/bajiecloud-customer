package com.bajiezu.cloud.customer.dal.mapper;

import com.bajiezu.cloud.customer.dal.entity.LabelInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LabelInfoMapper extends BaseMapper<LabelInfo> {

    LabelInfo queryByName(@Param("name") String name);

    List<LabelInfo> queryListBy(@Param("name") String name, @Param("status") Integer status, @Param("offset") Integer offset, @Param("limit") Integer limit);

    Long queryCountBy(@Param("name") String name, @Param("status") Integer status);

    List<LabelInfo> queryByIds(@Param("list") List<Long> ids);
}
