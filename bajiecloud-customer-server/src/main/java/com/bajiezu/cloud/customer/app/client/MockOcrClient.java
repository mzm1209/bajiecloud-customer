package com.bajiezu.cloud.customer.app.client;

import com.bajiezu.cloud.customer.app.vo.AppIdCardOcrBackVO;
import com.bajiezu.cloud.customer.app.vo.AppIdCardOcrFrontVO;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"local", "dev"})
public class MockOcrClient implements OcrClient {

    @Override
    public AppIdCardOcrFrontVO recognizeIdCardFront(String fileKey) {
        AppIdCardOcrFrontVO vo = new AppIdCardOcrFrontVO();
        vo.setRealName("张三");
        vo.setIdCard("522xxxxxxxxxxxxx");
        vo.setGender(1);
        vo.setBirthday("1998-01-01");
        return vo;
    }

    @Override
    public AppIdCardOcrBackVO recognizeIdCardBack(String fileKey) {
        AppIdCardOcrBackVO vo = new AppIdCardOcrBackVO();
        vo.setValidStart("2018-01-01");
        vo.setValidEnd("2038-01-01");
        return vo;
    }
}
