package com.bajiezu.cloud.customer.service;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.controller.customervo.CustomerBaseDetail;
import com.bajiezu.cloud.customer.controller.customervo.CustomerBaseReqVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerSensitiveReq;
import com.bajiezu.cloud.customer.controller.customervo.CustomerSensitiveResp;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.mapper.CustomerMapper;
import com.bajiezu.cloud.customer.enums.IRedisKey;
import com.bajiezu.cloud.customer.enums.RedisKeyEnum;
import com.bajiezu.cloud.customer.utils.IdCardUtil;
import com.bajiezu.cloud.customer.utils.JacksonUtil;
import com.bajiezu.cloud.customer.utils.ReflectUtils;
import com.bajiezu.cloud.marketing.api.vip.MarketingVipGradeApi;
import com.bajiezu.cloud.marketing.dto.vip.req.MarketingVipGradeReqDTO;
import com.bajiezu.cloud.marketing.dto.vip.resp.MarketingVipGradeRespDTO;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.CUSTOMER_NOT_EXIST;

@Slf4j
@Service
public class CustomerCacheServiceImpl implements CustomerCacheService {

    @Value("${spring.redis.key.prefix}")
    private String redisPrefix;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private MarketingVipGradeApi vipGradeApi;


    @Override
    public CustomerBaseDetail getBaseInfo(Long customerId) {
        return getBaseInfoFromDB(customerId);
    }


    private CustomerBaseDetail getBaseInfoFromDB(Long customerId) {
        CustomerBaseDetail baseDetail = new CustomerBaseDetail();
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            log.error("getBaseInfoFromDB CUSTOMER_NOT_EXIST");
            throw exception(CUSTOMER_NOT_EXIST);
        }
        BeanUtils.copyProperties(customer, baseDetail);
        baseDetail.setWechatId(customer.getWechatId());
        baseDetail.setWechatMobile(customer.getWechatMobile());
        baseDetail.setCustomerId(customer.getId());
        baseDetail.setRegisterTime(customer.getCreateTime());
        baseDetail.setInBlackList(customer.getInBlackList() ? 1 : 0);
        if (StringUtils.isNotEmpty(customer.getIdCard())) {
            Integer age = IdCardUtil.getAgeByIdCard(customer.getIdCard());
            baseDetail.setAge(age);
        }
        if (StringUtils.isNotEmpty(customer.getIdCard())) {
            baseDetail.setIdCard(IdCardUtil.desensitize(customer.getIdCard()));
        }

        Map<Long, String> memberLevelNameMap = Maps.newHashMap();
        MarketingVipGradeReqDTO gradeReqDTO = new MarketingVipGradeReqDTO();
        CommonResult<List<MarketingVipGradeRespDTO>> commonResult = vipGradeApi.getVipGradeList(gradeReqDTO);
        if (commonResult.isSuccess()) {
            List<MarketingVipGradeRespDTO> data = commonResult.getData();
            if (CollectionUtils.isNotEmpty(data)) {
                for (MarketingVipGradeRespDTO gradeRespDTO : data) {
                    memberLevelNameMap.put(gradeRespDTO.getId(), gradeRespDTO.getGradeName());
                }
            }
        }
        log.info("getBaseInfoFromDB query get memberLevelNameMap: {}", memberLevelNameMap);
        Integer memberLevel = customer.getMemberLevel();
        if (memberLevel != null && memberLevelNameMap.containsKey(memberLevel.longValue())) {
            baseDetail.setLevelName(memberLevelNameMap.get(memberLevel.longValue()));
        }
        return baseDetail;
    }


    @Override
    public CustomerSensitiveResp getCustomerSensitiveData(CustomerSensitiveReq reqVO) {
        log.info("getCustomerSensitiveData reqVO:{}", reqVO);
        Customer customer = customerMapper.selectById(reqVO.getCustomerId());
        if (customer == null) {
            log.error("getBaseInfoFromDB CUSTOMER_NOT_EXIST");
            throw exception(CUSTOMER_NOT_EXIST);
        }
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(reqVO.getParams()), "查询的params不能为空");
        CustomerSensitiveResp resp = new CustomerSensitiveResp();
        for (String param : reqVO.getParams()) {
            Object fieldValue = ReflectUtils.getFieldValue(customer, param);
            String fieldValueStr = fieldValue == null ? null : fieldValue.toString();
            ReflectUtils.setFieldValue(resp, param, fieldValueStr);
        }
        return resp;
    }


    @Override
    public void clearCache(Long customerId) {
        log.info("clear cache, id:{}", customerId);
        try {
            String cacheKey = RedisKeyEnum.CUSTOMER_BASE_INFO.prefixedFormat(redisPrefix, customerId);
            redisTemplate.delete(cacheKey);
        } catch (Exception e) {
            log.error("clear cache failed, customerId:{}, e = ", customerId, e);
        }
    }

    @Override
    public void batchClearCache(List<Long> customerIds) {
        if (CollectionUtils.isEmpty(customerIds)) {
            return;
        }
        List<String> keys = Lists.newArrayList();
        for (Long id : customerIds) {
            String cacheKey = RedisKeyEnum.CUSTOMER_BASE_INFO.prefixedFormat(redisPrefix, id);
            keys.add(cacheKey);
        }
        log.info("customer clear cache:{}", keys);
        try {
            redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("customer clear cache failed, ids:{}, e = ", customerIds, e);
        }
    }
}
