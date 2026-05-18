package com.bajiezu.cloud.customer.app.dto;

import lombok.Data;

@Data
public class AppIdentityListReqDTO {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
    private Integer authStatus;
    private String startTime;
    private String endTime;
}
