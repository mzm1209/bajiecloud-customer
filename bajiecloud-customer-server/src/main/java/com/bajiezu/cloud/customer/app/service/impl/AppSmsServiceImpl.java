package com.bajiezu.cloud.customer.app.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.bajiezu.cloud.customer.app.service.WeiDaoYunSmsService;
import com.bajiezu.cloud.customer.app.vo.wdy.SmsSendOneResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bajiezu.cloud.customer.app.dto.AppSmsSendReqDTO;
import com.bajiezu.cloud.customer.app.enums.AppSmsSceneEnum;
import com.bajiezu.cloud.customer.app.service.AppSmsService;
import com.bajiezu.cloud.customer.app.vo.AppSmsSendRespVO;
import com.bajiezu.cloud.customer.dal.entity.AppSmsCodeLogDO;
import com.bajiezu.cloud.customer.dal.mapper.AppSmsCodeLogMapper;
import com.bajiezu.cloud.system.api.third.SmsApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Objects;

import static com.bajiezu.cloud.common.web.exception.util.ServiceExceptionUtil.exception;
import static com.bajiezu.cloud.customer.enums.ErrorCodeConstants.LOGIN_EXCEPTION;
import static com.bajiezu.cloud.system.enums.ErrorCodeConstants.AUTH_LOGIN_SMS_SEND_FAIL;

@Slf4j
@Service
public class AppSmsServiceImpl implements AppSmsService {
    @Resource
    private AppSmsCodeLogMapper appSmsCodeLogMapper;
    @Resource
    private Environment environment;
    @Resource
    private SmsApi smsApi;
    @Resource
    private WeiDaoYunSmsService weiDaoYunSmsService;


    @Override
    public AppSmsSendRespVO sendLoginSms(AppSmsSendReqDTO reqDTO, String requestIp) {
        if (!AppSmsSceneEnum.LOGIN.name().equals(reqDTO.getScene())) throw exception(LOGIN_EXCEPTION);
        String mobileHash = SecureUtil.sha256(reqDTO.getCountryCode() + reqDTO.getMobile());
        Date now = new Date();
        Long cooldown = appSmsCodeLogMapper.selectCount(new LambdaQueryWrapper<AppSmsCodeLogDO>()
                .eq(AppSmsCodeLogDO::getMobileHash, mobileHash)
                .ge(AppSmsCodeLogDO::getCreateTime, DateUtil.offsetSecond(now, -300)));
        if (cooldown > 0) throw exception(AUTH_LOGIN_SMS_SEND_FAIL);

//        String code = RandomUtil.randomNumbers(6);
        String code = "123456";
        String salt = RandomUtil.randomString(16);
        String codeHash = SecureUtil.sha256(code + salt);
        String tpl = environment.getProperty("sms.weidaoyun.login-sms.verificationCodeContent");
        String defaultSignature = environment.getProperty("sms.weidaoyun.defaultSignature");
        if (StrUtil.hasBlank(tpl, defaultSignature)) throw exception(LOGIN_EXCEPTION);
        String content = MessageFormat.format(tpl, defaultSignature, code);
//        boolean sent = sendBySystemApi(buildPhone(reqDTO.getCountryCode(), reqDTO.getMobile()), content);
//        boolean sent = sendBySystemApi(reqDTO.getMobile(), content);
        boolean sent = true;
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

        SmsSendOneResponse smsSendOneResponse = weiDaoYunSmsService.sendSingleMessage(mobile, content);
        if (smsSendOneResponse.getCode() != 0) {
            log.error("发送短信失败，错误码 ：{}，错误信息：{}", smsSendOneResponse.getCode(), smsSendOneResponse.getMessage());
            throw exception(AUTH_LOGIN_SMS_SEND_FAIL);
        }else {
            return isSendSuccess(smsSendOneResponse);
        }
    }

    private String buildPhone(String countryCode, String mobile) {
        if (StrUtil.isBlank(countryCode)) {
            return mobile;
        }
        String cc = StrUtil.trim(countryCode).replace(" ", "");
        if (cc.startsWith("+")) {
            cc = cc.substring(1);
        }
        return cc + mobile;
    }

    private boolean isSendSuccess(Object response) {
        if (response == null) {
            return false;
        }
        try {
            Object code = response.getClass().getMethod("getCode").invoke(response);
            if (code instanceof Number number) {
                return number.intValue() == 0;
            }
            return Objects.equals("0", String.valueOf(code));
        } catch (Exception ignored) {
            log.warn("unable to parse sms response code, responseClass={}", response.getClass().getName());
            return false;
        }
    }
}
