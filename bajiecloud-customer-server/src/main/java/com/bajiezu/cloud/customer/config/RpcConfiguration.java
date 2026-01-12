package com.bajiezu.cloud.customer.config;

import com.bajiezu.cloud.marketing.api.vip.MarketingVipIntegralTaskApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "customerRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {MarketingVipIntegralTaskApi.class})
public class RpcConfiguration {

}