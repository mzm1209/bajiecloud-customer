package com.bajiezu.cloud.customer.controller;


import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "管理后台 - 客户中心")
@RestController
@RequestMapping("/customer/brand")
@Validated
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;



}
