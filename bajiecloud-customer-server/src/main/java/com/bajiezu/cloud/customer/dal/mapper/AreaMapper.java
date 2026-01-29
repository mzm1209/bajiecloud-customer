package com.bajiezu.cloud.customer.dal.mapper;

import com.bajiezu.cloud.customer.dal.entity.Area;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AreaMapper extends BaseMapper<Area> {

    List<Area> getAll();

    Area selectLatestByName(@Param("name") String name);

    List<Area> queryByName(@Param("name") String name);
}
