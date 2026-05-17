package com.bajiezu.cloud.customer.app.vo;

import lombok.Data;

@Data
public class AppIdCardOcrFrontVO {
    private String realName;
    private String idCard;
    private Integer gender;
    private String birthday;
    private String ethnicity;
    private String address;
}
