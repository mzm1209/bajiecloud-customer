package com.bajiezu.cloud.customer.service;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.bajiezu.cloud.common.web.pojo.CommonResult;
import com.bajiezu.cloud.common.web.pojo.PageResult;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerBehaviorListReqVO;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerBehaviorRespVO;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerBehaviorVO;
import com.bajiezu.cloud.customer.controller.customerbehaviorVO.CustomerTotalPointRespVO;
import com.bajiezu.cloud.customer.controller.customervo.CustomerBaseDetail;
import com.bajiezu.cloud.customer.controller.customervo.CustomerBaseReqVO;
import com.bajiezu.cloud.customer.dal.entity.CustomerBehaviorPointRecord;
import com.bajiezu.cloud.customer.dal.mapper.CustomerBehaviorPointRecordMapper;
import com.bajiezu.cloud.customer.enums.CustomerBehaviorPointEnum;
import com.bajiezu.cloud.framework.security.po.LoginUser;
import com.bajiezu.cloud.framework.security.util.SecurityFrameworkUtils;
import com.bajiezu.cloud.marketing.api.vip.MarketingVipIntegralTaskApi;
import com.bajiezu.cloud.marketing.dto.vip.req.MarketingVipIntegralTaskDetailReqDTO;
import com.bajiezu.cloud.marketing.dto.vip.resp.MarketingVipIntegralTaskDetailRespDTO;
import com.bajiezu.cloud.marketing.dto.vip.resp.base.TaskIntegralDTO;
import com.bajiezu.cloud.marketing.dto.vip.resp.base.TaskIntegralRuleDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;

@Slf4j
@Service
public class CustomerBehaviorServiceImpl implements CustomerBehaviorService{

    @Autowired
    private MarketingVipIntegralTaskApi taskApi;

    @Autowired
    private CustomerBehaviorPointRecordMapper customerBehaviorPointRecordMapper;

    @Autowired
    private CustomerCacheService customerCacheService;

    @Override
    public void handleCustomerBehavior(CustomerBehaviorVO vo) {
        log.info("handle customer behavior vo: {}", vo);
        vo.validateParams();

        CustomerBaseDetail customerBaseDetail =customerCacheService.getBaseInfo(vo.getCustomerId());
        if (customerBaseDetail == null) {
            log.error("handle customer behavior error, customer not exist, customerId:{}", vo.getCustomerId());
            return;
        }

        MarketingVipIntegralTaskDetailReqDTO reqDTO = new MarketingVipIntegralTaskDetailReqDTO();
        reqDTO.setTaskCondition(vo.getBehaviorCode());
        CommonResult<MarketingVipIntegralTaskDetailRespDTO> result = taskApi.getVipIntegralTaskDetail(reqDTO);
        if (result.isError()) {
            log.error("handle customer behavior error, get vip integral task detail error, result:{}", result);
            return;
        }
        MarketingVipIntegralTaskDetailRespDTO taskDetail = result.getData();
        log.info("handle customer get taskDetail : {}", taskDetail);

        if (taskDetail == null) {
            log.error("handle customer behavior error, get vip integral task detail error, taskDetail is null");
            return;
        }

        // 任务规则
        if (taskDetail.getTaskIntegral() != null) {
            TaskIntegralDTO taskIntegral = taskDetail.getTaskIntegral();
            if (!Objects.equals(taskIntegral.getTaskCondition(), vo.getBehaviorCode())) {
                log.error("handle customer behavior error, task condition not match, taskIntegral:{}", taskIntegral);
                return;
            }
            if (taskIntegral.getIntegralTaskStatus() != 1) {
                log.error("handle customer behavior error, task status not match, taskIntegral:{}", taskIntegral);
                return;
            }
            if (CollectionUtils.isEmpty(taskIntegral.getTaskIntegralRuleList())) {
                log.error("handle customer behavior error, taskIntegralRuleList is empty, taskIntegral:{}", taskIntegral);
                return;
            }
            Integer taskAction = taskIntegral.getTaskAction();
            List<TaskIntegralRuleDTO> rules = taskIntegral.getTaskIntegralRuleList();





        }

        // 全局规则
        if (CollectionUtils.isNotEmpty(taskDetail.getGlobalRuleList())) {

        }




    }

    @Override
    public CustomerTotalPointRespVO customerTotalPoint(CustomerBaseReqVO reqVO) {
        log.info("customerTotalPoint req: {}", reqVO);
        reqVO.validateParam();

        CustomerTotalPointRespVO respVO = new CustomerTotalPointRespVO();
        List<CustomerBehaviorPointRecord> records = customerBehaviorPointRecordMapper.queryAllBehaviorList(reqVO.getCustomerId());
        if (CollectionUtils.isEmpty(records)) {
            return respVO;
        }
        long totalPoint = 0L;
        long totalGrowth = 0L;
        for (CustomerBehaviorPointRecord record : records) {
            if (record.getOperatingAction() == 1) {
                totalPoint += record.getPointCount();
                totalGrowth += record.getGrowthCount();
            }
            if (record.getOperatingAction() == 2) {
                totalPoint -= record.getPointCount();
                totalGrowth -= record.getGrowthCount();
            }
        }
        log.info("customerTotalPoint totalPoint: {}, totalGrowth: {}", totalPoint, totalGrowth);
        respVO.setTotalPoint(totalPoint);
        respVO.setTotalGrowth(totalGrowth);
        return respVO;
    }

    @Override
    public PageResult<CustomerBehaviorRespVO> list(CustomerBehaviorListReqVO reqVO) {
        log.info("list req: {}", reqVO);
        reqVO.validateParam();

        LoginUser<?> loginUser = SecurityFrameworkUtils.getLoginUser();
        if (loginUser == null) {
            throw exception(LOGIN_EXCEPTION);
        }
        Integer offset = (reqVO.getPageNo() - 1) * reqVO.getPageSize();
        Integer limit = reqVO.getPageSize();

        List<CustomerBehaviorPointRecord> records = customerBehaviorPointRecordMapper.queryList(reqVO.getCustomerId(), offset, limit);
        if (CollectionUtils.isEmpty(records)) {
            return PageResult.empty();
        }
        Long count = customerBehaviorPointRecordMapper.queryCount(reqVO.getCustomerId());
        log.info("list query get count: {}", count);

        List<CustomerBehaviorRespVO> respVOList = Lists.newArrayList();
        for (CustomerBehaviorPointRecord record : records) {
            CustomerBehaviorRespVO respVO = new CustomerBehaviorRespVO();
            respVO.setId(record.getId());
            respVO.setBehaviorCode(record.getBehaviorCode());
            respVO.setBehaviorDesc(record.getBehaviorDesc());
            respVO.setOperateAction(record.getOperatingAction());
            respVO.setOperateTime(record.getCreateTime());
            if (reqVO.getBehaviorType() == 1) {
                respVO.setCount(record.getPointCount());
            }else {
                respVO.setCount(record.getGrowthCount());
            }
            respVOList.add(respVO);
        }
        return new PageResult<>(respVOList, count);
    }
}
