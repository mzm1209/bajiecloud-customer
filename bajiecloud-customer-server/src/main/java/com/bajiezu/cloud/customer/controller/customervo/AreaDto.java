package com.bajiezu.cloud.customer.controller.customervo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaDto implements Serializable {
    // 编码
    private String id;
    // 名称
    private String name;
    // 父区域编码
    private String pid;
    // 级别 1-省 2-市 3-区
    private int level;
}