package com.bajiezu.cloud.customer.enums;

import com.bajiezu.cloud.common.web.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode LOGIN_EXCEPTION = new ErrorCode(1_004_001_001, "用户登录异常");

    ErrorCode NAME_EXIST = new ErrorCode(1_004_001_002, "名称已存在");

    ErrorCode LABEL_NO_EXIST = new ErrorCode(1_004_001_003, "标签不存在");

    ErrorCode CUSTOMER_NOT_EXIST = new ErrorCode(1_004_001_004, "客户不存在");

    ErrorCode LABEL_HAVE_CUSTOMER = new ErrorCode(1_004_001_005, "标签关联客户无法删除");

    ErrorCode AREA_CODE_NO_EXIST = new ErrorCode(1_004_001_006, "地区编码不存在");


}
