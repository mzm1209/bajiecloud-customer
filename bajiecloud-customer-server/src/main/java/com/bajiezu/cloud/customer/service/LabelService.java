package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.vo.*;
import jakarta.servlet.http.HttpServletResponse;

public interface LabelService {

    void add(LabelAddReqVO reqVO);

    void mod(LabelModReqVO reqVO);

    void enable(LabelEnableReqVO reqVO);

    PageResult<LabelRespVO> list(LabelListReqVO reqVO);

    void export(LabelListReqVO reqVO, HttpServletResponse response);
}
