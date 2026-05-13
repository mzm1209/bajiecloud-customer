package com.bajiezu.cloud.customer.app.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bajiezu.cloud.customer.app.dto.AppSmsSendReqDTO;
import com.bajiezu.cloud.customer.app.enums.AppSmsSceneEnum;
import com.bajiezu.cloud.customer.app.service.AppSmsService;
import com.bajiezu.cloud.customer.app.vo.AppSmsSendRespVO;
import com.bajiezu.cloud.customer.dal.entity.AppSmsCodeLogDO;
import com.bajiezu.cloud.customer.dal.mapper.AppSmsCodeLogMapper;
import jakarta.annotation.Resource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;

@Service
public class AppSmsServiceImpl implements AppSmsService {
    @Resource
    private AppSmsCodeLogMapper appSmsCodeLogMapper;
    @Resource
    private Environment environment;
    @Resource(name = "weiDaoYunSmsServiceImpl")
    private Object weiDaoYunSmsService;

    @Override
    public AppSmsSendRespVO sendLoginSms(AppSmsSendReqDTO reqDTO, String requestIp) {
        if (!AppSmsSceneEnum.LOGIN.name().equals(reqDTO.getScene())) throw exception(LOGIN_EXCEPTION);
        String mobileHash = SecureUtil.sha256(reqDTO.getCountryCode() + reqDTO.getMobile());
        Date now = new Date();
        Long cooldown = appSmsCodeLogMapper.selectCount(new LambdaQueryWrapper<AppSmsCodeLogDO>()
                .eq(AppSmsCodeLogDO::getMobileHash, mobileHash)
                .ge(AppSmsCodeLogDO::getCreateTime, DateUtil.offsetSecond(now, -60)));
        if (cooldown > 0) throw exception(LOGIN_EXCEPTION);

        String code = RandomUtil.randomNumbers(6);
        String salt = RandomUtil.randomString(16);
        String codeHash = SecureUtil.sha256(code + salt);
        String tpl = environment.getProperty("sms.weidaoyun.login-sms.verificationCodeContent");
        if (StrUtil.isBlank(tpl)) throw exception(LOGIN_EXCEPTION);
        String content = MessageFormat.format(tpl, reqDTO.getCountryCode() + reqDTO.getMobile(), code);
        boolean sent = sendSms(reqDTO.getMobile(), content);

        AppSmsCodeLogDO log = new AppSmsCodeLogDO();
        log.setCountryCode(reqDTO.getCountryCode());
        log.setMobile(reqDTO.getMobile());
        log.setMobileHash(mobileHash);
        log.setScene(reqDTO.getScene());
        log.setSmsCodeHash(codeHash);
        log.setSalt(salt);
        log.setExpireTime(DateUtil.offsetMinute(now, 5));
        log.setVerifyStatus(0);
        log.setVerifyCount(0);
        log.setSendStatus(sent ? 1 : 0);
        log.setDeviceId(reqDTO.getDeviceId());
        log.setRequestIp(requestIp);
        log.setCreateTime(now);
        log.setUpdateTime(now);
        appSmsCodeLogMapper.insert(log);
        if (!sent) throw exception(LOGIN_EXCEPTION);
        AppSmsSendRespVO vo = new AppSmsSendRespVO();
        vo.setCooldownSeconds(60);
        vo.setExpireSeconds(300);
        return vo;
    }

    private boolean sendSms(String mobile, String content) {
        try {
            weiDaoYunSmsService.getClass().getMethod("sendSingleMessage", String.class, String.class)
                    .invoke(weiDaoYunSmsService, mobile, content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
