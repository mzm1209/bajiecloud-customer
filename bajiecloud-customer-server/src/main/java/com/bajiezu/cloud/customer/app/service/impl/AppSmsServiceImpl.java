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
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;

@Slf4j
@Service
public class AppSmsServiceImpl implements AppSmsService {
    @Resource
    private AppSmsCodeLogMapper appSmsCodeLogMapper;
    @Resource
    private Environment environment;
    @Resource
    private ApplicationContext applicationContext;

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
        boolean sent = sendBySystemApi(reqDTO.getMobile(), content);

        AppSmsCodeLogDO logDO = new AppSmsCodeLogDO();
        logDO.setCountryCode(reqDTO.getCountryCode());
        logDO.setMobile(reqDTO.getMobile());
        logDO.setMobileHash(mobileHash);
        logDO.setScene(reqDTO.getScene());
        logDO.setSendStatus(sent ? 1 : 0);
        if (sent) {
            logDO.setSmsCodeHash(codeHash);
            logDO.setSalt(salt);
            logDO.setExpireTime(DateUtil.offsetMinute(now, 5));
            logDO.setVerifyStatus(0);
            logDO.setVerifyCount(0);
        } else {
            logDO.setVerifyStatus(2);
            logDO.setVerifyCount(0);
        }
        logDO.setDeviceId(reqDTO.getDeviceId());
        logDO.setRequestIp(requestIp);
        logDO.setCreateTime(now);
        logDO.setUpdateTime(now);
        appSmsCodeLogMapper.insert(logDO);
        if (!sent) throw exception(LOGIN_EXCEPTION);

        AppSmsSendRespVO respVO = new AppSmsSendRespVO();
        respVO.setCooldownSeconds(60);
        respVO.setExpireSeconds(300);
        return respVO;
    }

    private boolean sendBySystemApi(String mobile, String content) {
        try {
            Object client = resolveSystemSmsClient();
            Method method = client.getClass().getMethod("sendSingleMessage", String.class, String.class);
            method.invoke(client, mobile, content);
            return true;
        } catch (Exception e) {
            log.warn("send sms via system-api failed: {}", e.getMessage());
            return false;
        }
    }

    private Object resolveSystemSmsClient() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(org.springframework.cloud.openfeign.FeignClient.class);
        for (Object bean : beans.values()) {
            if (bean.getClass().getName().contains("system") || bean.getClass().getName().contains("WeiDao")) {
                for (Method m : bean.getClass().getMethods()) {
                    if ("sendSingleMessage".equals(m.getName()) && m.getParameterCount() == 2) {
                        return bean;
                    }
                }
            }
        }
        throw exception(LOGIN_EXCEPTION);
    }
}
