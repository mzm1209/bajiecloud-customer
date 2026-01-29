package com.bajiezu.cloud.customer.service;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.bajiezu.cloud.customer.controller.customervo.AreaDto;
import com.bajiezu.cloud.customer.dal.entity.Area;
import com.bajiezu.cloud.customer.dal.mapper.AreaMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.AREA_CODE_NO_EXIST;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;

@Slf4j
@Component
public class AreaServiceImpl implements AreaService{

    // Area表的数据基本不会发生变化，因此只在启动时初始化地区数据到内存中。
    Map<String, AreaDto> cache = Maps.newHashMap();
    Map<String, List<AreaDto>> childrenCache = Maps.newHashMap();
    Map<String, String> fullNameCache = Maps.newHashMap();
    List<AreaDto> province = Lists.newArrayList();
    Map<String, String> areaNameCache = Maps.newHashMap();

    @Autowired
    private AreaMapper areaMapper;

    @PostConstruct
    public void init() {
        log.info("===== 开始执行区域缓存初始化方法 ====="); // 新增日志

        long time = System.currentTimeMillis();

        List<Area> list = areaMapper.getAll();
        log.info("区域数据查询结果：总条数={}，前3条数据={}", list.size(), list.stream().limit(3)
                .map(a -> "code=" + a.getCode() + ",pcode=" + a.getPcode() + ",level=" + a.getLevel()).collect(Collectors.toList()));

        // 直辖市的行政区域代码，降级为市级，代替"市辖区"。
        Set<String> directCities = Sets.newHashSet("110100", "120100", "310100", "500100");

        for (Area it : list) {
            AreaDto dto = new AreaDto(it.getCode(), it.getName(), it.getPcode(), it.getLevel());

            // 根据code建立索引
            cache.put(it.getCode(), dto);

            // 对地区全称建立索引
            if (it.getLevel() <= PROVINCE) {
                fullNameCache.put(it.getCode(), it.getName());
            } else {
                String parentName = fullNameCache.get(it.getPcode());
                if (directCities.contains(it.getCode())) {
                    // 对于4大直辖市，它们的名字不要显示为 "北京市-市辖区"，而是直接显示为"北京市"
                    fullNameCache.put(it.getCode(), parentName);
                    it.setName(parentName);// 这个改动是为了让getName方法能够直接得到"北京市"、"天津市"这种名称
                } else {
                    fullNameCache.put(it.getCode(), parentName + "-" + it.getName());
                }
            }

            // 缓存省级行政区域
            if (it.getLevel() == PROVINCE) {
                province.add(dto);
            }

            // 根据父节点的code建立索引
            List<AreaDto> children = childrenCache.computeIfAbsent(it.getPcode(),
                    k -> Lists.newArrayList());
            children.add(dto);
        }

        initAreaName();

        time = System.currentTimeMillis() - time;
        log.info("Area cache is initialized in {} ms.", time);
    }

    @Override
    public String getName(String code) {
        AreaDto area = cache.get(code);
        if (area == null) {
            log.warn("area not exist:{}", code);
            return null;
        }
        return area.getName();
    }

    @Override
    public Map<String, String> getNames(Collection<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            log.warn("area codes is empty");
            throw exception(AREA_CODE_NO_EXIST);
        }

        Map<String, String> map = Maps.newHashMap();
        for (String code : codes) {
            AreaDto area = cache.get(code);
            if (area != null) {
                map.put(code, area.getName());
            }
        }

        return map;
    }

    @Override
    public String getFullName(String code) {
        if (!fullNameCache.containsKey(code)) {
            throw exception(AREA_CODE_NO_EXIST);
        }
        return fullNameCache.get(code);
    }

    @Override
    public Map<String, String> getFullNames(Collection<String> codes) {
        if (CollectionUtils.isEmpty(codes)) {
            throw exception(AREA_CODE_NO_EXIST);
        }

        Map<String, String> map = Maps.newHashMap();
        for (String code : codes) {
            if (code == null) {
                continue;
            }
            map.put(code, fullNameCache.get(code));
        }

        return map;
    }

    @Override
    public List<String> getChildrenIds(String code, boolean containSelf) {
        AreaDto area = cache.get(code);
        if (area == null) {
            log.warn("area node found:{}", code);
            throw exception(AREA_CODE_NO_EXIST);
        }

        // 已经是最下级区域，没有更下级的区域了
        if (area.getLevel() >= REGION) {
            if (containSelf) {
                return Collections.singletonList(code);
            } else {
                return Collections.emptyList();
            }
        }

        // 可能是省或市这两级行政区域
        List<String> result = Lists.newArrayList();
        if (containSelf) {
            result.add(code);
        }

        List<AreaDto> children = childrenCache.get(code);
        if (CollectionUtils.isNotEmpty(children)) {
            children.forEach(it -> result.add(it.getId()));
        }

        // 对于市级行政区，只有1层下级区域，直接返回即可。
        if (area.getLevel() == CITY) {
            return result;
        }

        if (CollectionUtils.isNotEmpty(children)) {
            // 否则就是省级行政区了
            for (AreaDto it : children) {
                List<AreaDto> region = childrenCache.get(it.getId());
                if (CollectionUtils.isNotEmpty(region)) {
                    region.forEach(r -> result.add(r.getId()));
                }
            }
        }
        return result;
    }

    @Override
    public List<String> getDirectChildrenIds(String code, boolean containSelf) {
        AreaDto area = cache.get(code);
        if (area == null) {
            log.warn("area node found:{}", code);
            throw exception(AREA_CODE_NO_EXIST);
        }

        // 已经是最下级区域，没有更下级的区域了
        if (area.getLevel() >= REGION) {
            if (containSelf) {
                return Collections.singletonList(code);
            } else {
                return Collections.emptyList();
            }
        }

        // 可能是省或市这两级行政区域
        List<String> result = Lists.newArrayList();
        if (containSelf) {
            result.add(code);
        }

        List<AreaDto> children = childrenCache.get(code);
        if (CollectionUtils.isNotEmpty(children)) {
            children.forEach(it -> result.add(it.getId()));
        }

        return result;
    }

    @Override
    public Map<String, List<String>> getDirectChildrenIds(List<String> ids, boolean containSelf) {
        Map<String, List<String>> codes = Maps.newHashMap();
        for (String code : ids) {
            List<String> directChildrenResult = getDirectChildrenIds(code, containSelf);
            if (CollectionUtils.isNotEmpty(directChildrenResult)) {
                codes.put(code, directChildrenResult);
            }
        }
        return codes;
    }

    @Override
    public List<AreaDto> getProvince() {
        return province;
    }

    @Override
    public List<AreaDto> getChildren(String code) {
        if (childrenCache.containsKey(code)) {
            return childrenCache.get(code);
        } else {
            throw exception(AREA_CODE_NO_EXIST);
        }
    }

    @Override
    public Map<String, AreaDto> getAll() {
        return cache;
    }

    @Override
    public AreaDto getAreaByName(String name) {
        log.info("getAreaByName name: {}", name);
        List<Area> areas = areaMapper.queryByName(name);
        AreaDto areaDto = null;
        if (CollectionUtils.isEmpty(areas)) {
            log.info("getAreaByName get empty area by name: {}", name);
            return areaDto;
        }
        if (areas.size() == 1) {
            Area area = areas.get(0);
            if (area.getLevel() == PROVINCE) {
                areaDto = buildDto(area);
            }
            if (area.getLevel() == CITY) {
                String pcode = area.getPcode();
                areaDto = cache.get(pcode);
            }
            if (area.getLevel() == REGION) {
                String cityCode = area.getPcode();
                AreaDto cityAreaDto = cache.get(cityCode);
                if (cityAreaDto != null) {
                    String pPcode = cityAreaDto.getPid();
                    areaDto = cache.get(pPcode);
                }
            }
        }
        if (areas.size() > 1) {
            List<Area> area = areas.stream().filter(a->a.getLevel() == CITY).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(area)) {
                Area cityArea = areas.get(0);
                String pcode = cityArea.getPcode();
                areaDto = cache.get(pcode);
            }else {
                Area regionArea = areas.get(0);
                String cityCode = regionArea.getPcode();
                AreaDto cityAreaDto = cache.get(cityCode);
                if (cityAreaDto != null) {
                    String pPcode = cityAreaDto.getPid();
                    areaDto = cache.get(pPcode);
                }
            }
        }
        return areaDto;
    }

    private AreaDto buildDto(Area area) {
        AreaDto dto = new AreaDto();
        dto.setName(area.getName());
        dto.setId(area.getCode());
        dto.setLevel(area.getLevel());
        dto.setPid(area.getPcode());
        return dto;
    }

    private void initAreaName() {
        Map<String, String> nameCache = Maps.newHashMap();

        // 查询所有地区
        Map<String, AreaDto> areaDtoMap = this.cache;
        Collection<AreaDto> areas = areaDtoMap.values();

        Map<String, String> pmap = Maps.newHashMap();
        pmap.put("内蒙古自治区", "内蒙古");
        pmap.put("广西壮族自治区", "广西");
        pmap.put("西藏自治区", "西藏");
        pmap.put("宁夏回族自治区", "宁夏");
        pmap.put("新疆维吾尔自治区", "新疆");
        pmap.put("香港特别行政区", "香港");
        pmap.put("澳门特别行政区", "澳门");

        // 省级行政区
        areas.stream()// 省级行政区
                .filter(it -> it.getLevel() == AreaService.PROVINCE)
                .forEach(it -> {
                    String name = it.getName();
                    nameCache.put(name, it.getId());
                    // 把省的简称也放进去，例如 "湖北省" = "湖北" -> 420000
                    if (name.endsWith("省")) {
                        name = name.substring(0, name.length() - 1);
                        nameCache.put(name, it.getId());
                    }
                    // 自治区的简称
                    else if (pmap.containsKey(name)) {
                        nameCache.put(pmap.get(name), it.getId());
                    }
                });

        areas.stream()// 市、区级行政区，忽略"市辖区"这种无法确认地点的名称
                .filter(it -> it.getLevel() > AreaService.PROVINCE
                        && !"市辖区".equals(it.getName()))
                .forEach(it -> {
                    String name = it.getName();
                    nameCache.put(name, it.getId());
                    // 把市的简称也放进去，例如 "武汉市" = "武汉" -> 420100
                    if (name.endsWith("市")) {
                        name = name.substring(0, name.length() - 1);
                        nameCache.put(name, it.getId());
                    }
                });

        // 直辖市的行政区域代码，降级为市级，代替"市辖区"。
        nameCache.put("北京市", "110100");
        nameCache.put("北京", "110100");
        nameCache.put("天津市", "120100");
        nameCache.put("天津", "120100");
        nameCache.put("上海市", "310100");
        nameCache.put("上海", "310100");
        nameCache.put("重庆市", "500100");
        nameCache.put("重庆", "500100");

        areaNameCache.putAll(nameCache);
    }

    @Override
    public String getAreaIdByName(String name) {
        return areaNameCache.get(name);
    }
}
