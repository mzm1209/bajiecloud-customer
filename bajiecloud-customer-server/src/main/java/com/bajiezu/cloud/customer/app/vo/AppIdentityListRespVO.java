package com.bajiezu.cloud.customer.app.vo;

import lombok.Data;

import java.util.List;

@Data
public class AppIdentityListRespVO {
    private Long total;
    private List<AppIdentityListItemVO> list;
}
