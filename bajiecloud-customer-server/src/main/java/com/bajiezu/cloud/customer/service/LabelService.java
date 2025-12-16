package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.vo.*;

public interface LabelService {

    void add(LabelAddReqVO reqVO);

    void mod(LabelModReqVO reqVO);

    void enable(LabelEnableReqVO reqVO);

    PageResult<LabelRespVO> list(LabelListReqVO reqVO);

    void export(LabelListReqVO reqVO);
}
