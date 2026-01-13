package com.bajiezu.cloud.customer.service;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.bajiezu.cloud.customer.controller.customervo.CustomerBaseDetail;
import com.bajiezu.cloud.customer.controller.customervo.CustomerBaseReqVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerSensitiveReq;
import com.bajiezu.cloud.customer.controller.customervo.CustomerSensitiveResp;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.mapper.CustomerMapper;
import com.bajiezu.cloud.customer.enums.IRedisKey;
import com.bajiezu.cloud.customer.enums.RedisKeyEnum;
import com.bajiezu.cloud.customer.utils.JacksonUtil;
import com.bajiezu.cloud.customer.utils.ReflectUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

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


    @Override
    public CustomerBaseDetail getBaseInfo(Long customerId) {
        IRedisKey key = RedisKeyEnum.CUSTOMER_BASE_INFO;
        String cacheKey = key.prefixedFormat(redisPrefix, customerId);
        CustomerBaseDetail baseDetail;

        String json = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isNotBlank(json)) {
            try {
                baseDetail = JacksonUtil.str2Obj(json, CustomerBaseDetail.class);
                return baseDetail;
            } catch (IOException e) {
                log.error("read redis getBaseInfo json failed, json={}", json, e);
            }
        }

        baseDetail = getBaseInfoFromDB(customerId);
        redisTemplate.opsForValue().set(cacheKey, JacksonUtil.obj2Str(baseDetail), key.getExpireTime(), key.getTimeUnit());
        return baseDetail;
    }


    private CustomerBaseDetail getBaseInfoFromDB(Long customerId) {
        CustomerBaseDetail baseDetail = new CustomerBaseDetail();
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            log.error("getBaseInfoFromDB CUSTOMER_NOT_EXIST");
            throw exception(CUSTOMER_NOT_EXIST);
        }
        BeanUtils.copyProperties(customer, baseDetail);
        baseDetail.setCustomerId(customer.getId());
        return baseDetail;
    }


    @Override
    public List<CustomerSensitiveResp> getCustomerSensitiveData(CustomerSensitiveReq reqVO) {
        log.info("getCustomerSensitiveData reqVO:{}", reqVO);
        Customer customer = customerMapper.selectById(reqVO.getCustomerId());
        if (customer == null) {
            log.error("getBaseInfoFromDB CUSTOMER_NOT_EXIST");
            throw exception(CUSTOMER_NOT_EXIST);
        }
        Preconditions.checkArgument(CollectionUtils.isNotEmpty(reqVO.getParams()), "查询的params不能为空");
        List<CustomerSensitiveResp> respVOList = Lists.newArrayList();
        for (String param : reqVO.getParams()) {
            Object fieldValue = ReflectUtils.getFieldValue(customer, param);

            CustomerSensitiveResp resp = new CustomerSensitiveResp();
            resp.setParam( param);
            String fieldValueStr = fieldValue == null ? "" : fieldValue.toString();
            resp.setValue(fieldValueStr);
            respVOList.add(resp);
        }
        return respVOList;
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
