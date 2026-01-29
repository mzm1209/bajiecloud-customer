package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.customer.controller.customervo.AreaDto;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface AreaService {

    // 地区级别
    int PROVINCE = 1;// 省

    int CITY = 2;// 市

    int REGION = 3;// 区

    /**
     * 移除地区ID末尾的'00'，获得前缀编码，用于通过 like 查询 sql 中的子地区数据。
     *
     * eg1. '140200' -> '1402',
     * eg2. select * from xx where area_id lile '1402%'; 可以查询到所有'140200'的下级地区数据。
     *
     * @param id 地区ID
     * @return 地区前缀编码
     */
    default String getPrefix(String id) {
        if (id == null || id.length() != 6) {
            throw new IllegalArgumentException("invalid area id:" + id);
        }
        if (id.endsWith("0000")) {
            return id.substring(0, 2);
        } else if (id.endsWith("00")) {
            return id.substring(0, 4);
        } else {
            return id;
        }
    }

    /**
     * 根据地区编码，判断childCode是否为parentCode的子节点。
     *
     * eg1. parentCode = '110000', childCode = '110100', => true
     * eg2. parentCode = '110000', childCode = '110101', => true
     * eg3. parentCode = '110000', childCode = '120103', => false
     *
     * @param parentCode 父级地区ID
     * @param childCode 子地区ID
     * @return true 是 false 否
     */
    default boolean isChild(String parentCode, String childCode) {
        if (StringUtils.isAnyBlank(parentCode, childCode)) {
            throw new IllegalArgumentException("invalid area id");
        }

        String prefix = getPrefix(parentCode);
        return childCode.startsWith(prefix);
    }

    /**
     * 查询当前地区的级别
     *
     * @param id 地区ID
     * @return 地区级别 1-省 2-市 3-区
     */
    default int getLevel(String id) {
        if (id == null || id.length() != 6) {
            throw new IllegalArgumentException("invalid area id:" + id);
        }
        if (id.endsWith("0000")) {
            return PROVINCE;
        } else if (id.endsWith("00")) {
            return CITY;
        } else {
            return REGION;
        }
    }

    /**
     * 获得当前区域ID的省级行政区域ID
     *
     * @param id 地区ID
     * @return 地市的省级地区ID
     */
    default String getProvinceId(String id) {
        if (id == null || id.length() != 6) {
            throw new IllegalArgumentException("invalid area id:" + id);
        }
        if (id.endsWith("0000")) {
            return id;
        } else {
            return id.substring(0, 2) + "0000";
        }
    }

    /**
     * 获得当前区域ID的市级行政区域ID
     *
     * @param id 地区ID
     * @return 区的市级ID
     */
    default String getCityId(String id) {
        if (id == null || id.length() != 6) {
            throw new IllegalArgumentException("invalid area id:" + id);
        }
        if (id.endsWith("0000")) {
            throw new IllegalArgumentException("Can't get parent city of a province:" + id);
        } else if (id.endsWith("00")) {
            return id;
        } else {
            return id.substring(0, 4) + "00";
        }
    }

    /**
     * 获取指定区域的名称
     *
     * eg: "420111" -> "洪山区"
     *
     * @param id 行政区域编码
     * @return 地区名称
     */
    String getName(String id);

    /**
     * 批量查询区域的名称
     *
     * @param ids 行政区域编码
     * @return key:地区编码 value:地区名称
     */
    Map<String, String> getNames(Collection<String> ids);

    /**
     * 获得指定区域的完整名称
     *
     * eg: "420111" -> "湖北省-武汉市-洪山区"
     *
     * @param id 行政区域编码
     * @return 地区的完整名称
     */
    String getFullName(String id);

    /**
     * 批量查询区域的完整名称
     *
     * @param ids 行政区域编码
     * @return key:地区编码 value:地区的完整名称
     */
    Map<String, String> getFullNames(Collection<String> ids);

    /**
     * 查询指定区域的所有下级区域编码，递归
     *
     * @param id 行政区域编码
     * @param containSelf 是否包含自身
     * @return 下级区域的ID集合
     */
    List<String> getChildrenIds(String id, boolean containSelf);

    /**
     * 查询指定区域的所有直接下级区域编码，不递归
     *
     * @param id 行政区域编码
     * @param containSelf 是否包含自身
     * @return 直接下级区域的ID集合
     */
    List<String> getDirectChildrenIds(String id, boolean containSelf);

    /**
     * 查询指定区域的所有直接下级区域编码，不递归
     *
     * @param ids 行政区域编码
     * @param containSelf 是否包含自身
     * @return 直接下级区域的ID集合
     */
    Map<String, List<String>> getDirectChildrenIds(List<String> ids, boolean containSelf);

    /**
     * 查询所有省级区域
     *
     * @return 所有省级区域
     */
    List<AreaDto> getProvince();

    /**
     * 根据区域编码，查询直接下级区域（不递归）
     *
     * @param id 行政区域编码
     * @return 下级区域
     */
    List<AreaDto> getChildren(String id);

    Map<String, AreaDto> getAll();

    /**
     *  根据名称匹配area
     *  获取省信息
     * */
    AreaDto getAreaByName(String name);

    String getAreaIdByName(String name);
}
