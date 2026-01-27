package com.bajiezu.cloud.customer.dal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CustomerListDto {

    private String mobile;

    private String name;

    private Long customerId;

    private List<Integer> memberLevels;

    private List<String> platformNames;

    private List<String> thirdPartyIds;

    private Integer isBlackCustomer;

    private Integer offset;

    private Integer limit;
}
