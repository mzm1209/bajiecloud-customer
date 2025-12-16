package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.vo.*;

public interface LabelService {

    void add(LabelAddReqVO reqVO);

    void edit(LabelModReqVO reqVO);

    void enabel(LabelEnableReqVO reqVO);

    PageResult<LabelRespVO> list(LabelListReqVO reqVO);

    void export();
}
