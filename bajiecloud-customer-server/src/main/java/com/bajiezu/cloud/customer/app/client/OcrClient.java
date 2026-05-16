package com.bajiezu.cloud.customer.app.client;

import com.bajiezu.cloud.customer.app.vo.AppIdCardOcrBackVO;
import com.bajiezu.cloud.customer.app.vo.AppIdCardOcrFrontVO;

public interface OcrClient {

    AppIdCardOcrFrontVO recognizeIdCardFront(String fileKey);

    AppIdCardOcrBackVO recognizeIdCardBack(String fileKey);
}
