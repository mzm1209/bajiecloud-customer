package com.bajiezu.cloud.customer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bajiezu.cloud.customer.dal.mapper.**") // 指定Mapper接口的包路径
public class CustomerServerApplication {


    public static void main(String[] args) {
        SpringApplication.run(CustomerServerApplication.class, args);
    }


}
