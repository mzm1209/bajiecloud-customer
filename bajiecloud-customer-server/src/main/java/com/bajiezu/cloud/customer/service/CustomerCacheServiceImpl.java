package com.bajiezu.cloud.customer.service;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.bajiezu.cloud.customer.controller.customervo.CustomerBaseDetail;
import com.bajiezu.cloud.customer.dal.entity.Customer;
import com.bajiezu.cloud.customer.dal.mapper.CustomerMapper;
import com.bajiezu.cloud.customer.enums.IRedisKey;
import com.bajiezu.cloud.customer.enums.RedisKeyEnum;
import com.bajiezu.cloud.customer.utils.JacksonUtil;
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
        if (baseDetail == null) {
            return null;
        }
        redisTemplate.opsForValue().set(cacheKey, JacksonUtil.obj2Str(baseDetail), key.getExpireTime(), key.getTimeUnit());
        return baseDetail;
    }


    private CustomerBaseDetail getBaseInfoFromDB(Long customerId) {
        CustomerBaseDetail baseDetail = new CustomerBaseDetail();
        Customer customer = customerMapper.selectById(customerId);
        if (customer == null) {
            return null;
        }
        BeanUtils.copyProperties(customer, baseDetail);
        baseDetail.setCustomerId(customer.getId());
        return baseDetail;
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
