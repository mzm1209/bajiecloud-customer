package com.bajiezu.cloud.customer.app.service;

import com.bajiezu.cloud.customer.app.dto.AppIdentityListReqDTO;
import com.bajiezu.cloud.customer.app.dto.AppRealnameSubmitReqDTO;
import com.bajiezu.cloud.customer.app.vo.AppIdentityDetailRespVO;
import com.bajiezu.cloud.customer.app.vo.AppIdentityListRespVO;
import com.bajiezu.cloud.customer.app.vo.AppIdCardUploadRespVO;
import com.bajiezu.cloud.customer.app.vo.AppRealnameStatusRespVO;
import com.bajiezu.cloud.customer.app.vo.AppRealnameSubmitRespVO;
import org.springframework.web.multipart.MultipartFile;

public interface AppRealnameService {

    AppIdCardUploadRespVO uploadIdCard(String side, MultipartFile file);

    AppRealnameSubmitRespVO submit(AppRealnameSubmitReqDTO reqDTO);

    AppRealnameStatusRespVO getStatus();

    AppIdentityListRespVO identityList(AppIdentityListReqDTO reqDTO);

    AppIdentityDetailRespVO identityDetail(Long id);

}
