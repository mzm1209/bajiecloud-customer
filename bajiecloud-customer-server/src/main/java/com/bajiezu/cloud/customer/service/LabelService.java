package com.bajiezu.cloud.customer.service;

import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.labelvo.*;
import com.bajiezu.cloud.customer.utils.Id2NameDto;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface LabelService {

    void add(LabelAddReqVO reqVO);

    void mod(LabelModReqVO reqVO);

    void delete(LabelIdReqVO reqVO);

    void enable(LabelEnableReqVO reqVO);

    PageResult<LabelRespVO> list(LabelListReqVO reqVO);

    List<Id2NameDto> queryAllLabel();

}
