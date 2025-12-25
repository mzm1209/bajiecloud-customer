package com.bajiezu.cloud.customer.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "customerRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {})
public class RpcConfiguration {

}