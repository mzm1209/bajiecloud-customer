package com.bajiezu.cloud.customer;

import com.bajiezu.cloud.rocketmq.config.RocketMQProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.bajiezu.cloud.customer.dal.mapper.**") // 指定Mapper接口的包路径
@EnableFeignClients(basePackages = "com.bajiezu.cloud.system.api")
@EnableConfigurationProperties(RocketMQProperties.class)
public class CustomerServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(CustomerServerApplication.class, args);
    }


}
