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
import com.bajiezu.cloud.marketing.dto.vip.resp.base.GlobalIntegralDTO;
import com.bajiezu.cloud.marketing.dto.vip.resp.base.TaskIntegralDTO;
import com.bajiezu.cloud.marketing.dto.vip.resp.base.TaskIntegralRuleDTO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

        CustomerBehaviorPointRecord pointRecord = new CustomerBehaviorPointRecord();
        pointRecord.setCustomerId(vo.getCustomerId());
        pointRecord.setBehaviorCode(vo.getBehaviorCode());
        pointRecord.setBehaviorDesc(CustomerBehaviorPointEnum.getEnums(vo.getBehaviorCode()).getDesc());
        pointRecord.setCreateBy(-1L);
        pointRecord.setUpdatedBy(-1L);
        pointRecord.setCreateTime(new Date());
        pointRecord.setUpdateTime(new Date());

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
            if  (CollectionUtils.isEmpty(rules)) {
                log.error("handle customer behavior error, taskIntegralRuleList is empty, taskIntegral:{}", taskIntegral);
                return;
            }
            pointRecord.setSettingType(1);
            pointRecord.setOperatingAction(taskAction);
            pointRecord.setTaskId(taskIntegral.getIntegralTaskId());

            List<CustomerBehaviorPointRecord> existRecords = customerBehaviorPointRecordMapper.queryByCustomerIdAndRuleId(vo.getCustomerId().intValue(), taskIntegral.getIntegralTaskId(), null);
            Long totalGrowth = 0L;
            Long totalPoint = 0L;
            if (CollectionUtils.isNotEmpty(existRecords)) {
                for (CustomerBehaviorPointRecord record : existRecords) {
                    totalGrowth += record.getGrowthCount();
                    totalPoint += record.getPointCount();
                }
            }
            log.info("handle customer behavior  totalGrowth: {}, totalPoint: {}", totalGrowth, totalPoint);

            if (CustomerBehaviorPointEnum.getEnums(vo.getBehaviorCode()) == CustomerBehaviorPointEnum.REPAYMENT_OVERDUE) {
                Integer days = vo.getDays();
                TaskIntegralRuleDTO ruleDTO = rules.stream().filter(dto ->  dto.getDays() != null && dto.getDays().equals(days)).findFirst().orElse(null);
                if (ruleDTO != null) {
                    fixPointRecord(pointRecord, ruleDTO, totalGrowth, totalPoint);
                }else {
                    log.info("handle customer behavior error, ruleDTO is empty, taskIntegral:{}", taskIntegral);
                }
            }else {
                for (TaskIntegralRuleDTO dto : rules) {
                    fixPointRecord(pointRecord, dto, totalGrowth, totalPoint);
                }
            }
            log.info("handle customer behavior, pointRecord: {}", pointRecord);
            customerBehaviorPointRecordMapper.insert(pointRecord);
        }

        // 全局规则
        if (CollectionUtils.isNotEmpty(taskDetail.getGlobalRuleList())) {
            // todo 自定义有效期 暂不处理
            List<GlobalIntegralDTO> globalRuleList = taskDetail.getGlobalRuleList().stream().filter(dto -> (dto.getIntegralRuleStatus() == 1 && dto.getIntegralValidityType() == 1)).toList();
            if (CollectionUtils.isEmpty(globalRuleList)) {
                log.error("handle customer behavior error, globalRuleList is empty, taskDetail:{}", taskDetail);
                return;
            }
            pointRecord.setSettingType(2);
            pointRecord.setOperatingAction(2);

            GlobalIntegralDTO globalRule = globalRuleList.get(0);
            pointRecord.setTaskId(globalRule.getId());
            pointRecord.setPointCount(vo.getPoints().longValue());
            customerBehaviorPointRecordMapper.insert(pointRecord);
        }
    }


    private void fixPointRecord(CustomerBehaviorPointRecord pointRecord, TaskIntegralRuleDTO dto, Long totalGrowth, Long totalPoint) {
        pointRecord.setRuleId(dto.getRuleId().longValue());
        if (!dto.getGrowthValueUpperStatus()) {
            pointRecord.setGrowthCount(dto.getGrowthValue().longValue());
        }else {
            // 设置了成长值上线 则判断当前任务规则的成长值上限是否满足 上线
            if (totalGrowth < dto.getGrowthValueUpper()) {
                // 如果当前值 + 当前任务规则的成长值 < 上线 则直接入库， 如果大于，则入差值
                if (totalGrowth + dto.getGrowthValue().longValue() < dto.getGrowthValueUpper()) {
                    pointRecord.setGrowthCount(dto.getGrowthValue().longValue());
                }else {
                    pointRecord.setGrowthCount(dto.getGrowthValueUpper() - totalGrowth);
                }
            }else {
                log.info("handle customer behavior error, growth value upper status, growth value upper:{}", dto.getGrowthValueUpper());
            }
        }
        if (!dto.getGiftIntegralUpperStatus()) {
            pointRecord.setPointCount(dto.getGiftIntegral().longValue());
        }else {
            if (totalPoint < dto.getGiftIntegralUpper()) {
                if (totalPoint + dto.getGiftIntegral().longValue() < dto.getGiftIntegralUpper()) {
                    pointRecord.setPointCount(dto.getGiftIntegral().longValue());
                }else {
                    pointRecord.setPointCount(dto.getGiftIntegralUpper() - totalPoint);
                }
            }else {
                log.info("handle customer behavior error, gift integral upper status, gift integral upper:{}", dto.getGiftIntegralUpper());
            }
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
