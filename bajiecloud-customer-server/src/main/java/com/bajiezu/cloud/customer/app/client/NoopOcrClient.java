package com.bajiezu.cloud.customer.app.client;

import com.bajiezu.cloud.customer.app.vo.AppIdCardOcrBackVO;
import com.bajiezu.cloud.customer.app.vo.AppIdCardOcrFrontVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(OcrClient.class)
public class NoopOcrClient implements OcrClient {
    @Override
    public AppIdCardOcrFrontVO recognizeIdCardFront(String fileKey) {
        return null;
    }

    @Override
    public AppIdCardOcrBackVO recognizeIdCardBack(String fileKey) {
        return null;
    }
}
