package com.bajiezu.cloud.customer.enums;

import com.bajiezu.cloud.common.web.exception.ErrorCode;

public interface ErrorCodeConstants {

    ErrorCode LOGIN_EXCEPTION = new ErrorCode(1, "用户登录异常");

    ErrorCode NAME_EXIST = new ErrorCode(2, "名称已存在");

    ErrorCode LABEL_NO_EXIST = new ErrorCode(3, "标签不存在");

    ErrorCode CUSTOMER_NOT_EXIST = new ErrorCode(4, "客户不存在");

}
