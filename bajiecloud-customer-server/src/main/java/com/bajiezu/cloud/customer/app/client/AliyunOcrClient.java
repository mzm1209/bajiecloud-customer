package com.bajiezu.cloud.customer.app.client;

import com.aliyun.sdk.service.ocr_api20210707.AsyncClient;
import com.aliyun.sdk.service.ocr_api20210707.models.RecognizeIdcardRequest;
import com.aliyun.sdk.service.ocr_api20210707.models.RecognizeIdcardResponse;
import com.bajiezu.cloud.customer.app.vo.AppIdCardOcrBackVO;
import com.bajiezu.cloud.customer.app.vo.AppIdCardOcrFrontVO;
import com.bajiezu.cloud.customer.utils.IdCardUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@ConditionalOnBean(AsyncClient.class)
public class AliyunOcrClient implements OcrClient {
    private static final Pattern VALID_PERIOD_PATTERN = Pattern.compile("(\\d{4})[.\\-/]?(\\d{2})[.\\-/]?(\\d{2}).*?(\\d{4})[.\\-/]?(\\d{2})[.\\-/]?(\\d{2})");
    @Resource
    private AsyncClient aliyunOcrAsyncClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private OssPrivateFileService ossPrivateFileService;

    @Override
    public AppIdCardOcrFrontVO recognizeIdCardFront(String fileKey) {
        String data = call(fileKey);
        if (!StringUtils.hasText(data)) return null;
        try {
            JsonNode d = objectMapper.readTree(data).path("face").path("data");
            if (d.isMissingNode() || d.isNull()) return null;
            AppIdCardOcrFrontVO vo = new AppIdCardOcrFrontVO();
            vo.setRealName(text(d,"name"));
            vo.setIdCard(IdCardUtil.desensitize(text(d,"idNumber")));
            vo.setBirthday(text(d,"birthDate"));
            vo.setEthnicity(text(d,"ethnicity"));
            vo.setAddress(maskAddress(text(d,"address")));
            String sex=text(d,"sex");
            vo.setGender("男".equals(sex)?1:"女".equals(sex)?2:0);
            return vo;
        } catch (Exception e){return null;}
    }

    @Override
    public AppIdCardOcrBackVO recognizeIdCardBack(String fileKey) {
        String data = call(fileKey);
        if (!StringUtils.hasText(data)) return null;
        try {
            JsonNode d = objectMapper.readTree(data).path("back").path("data");
            if (d.isMissingNode() || d.isNull()) return null;
            AppIdCardOcrBackVO vo = new AppIdCardOcrBackVO();
            vo.setIssueAuthority(maskAddress(text(d,"issueAuthority")));
            String vp=text(d,"validPeriod");
            if (StringUtils.hasText(vp)) {
                if ("长期".equals(vp.trim())) { vo.setValidEnd("长期"); }
                Matcher m=VALID_PERIOD_PATTERN.matcher(vp.replace("至","-"));
                if (m.find()) { vo.setValidStart(m.group(1)+"-"+m.group(2)+"-"+m.group(3)); vo.setValidEnd(m.group(4)+"-"+m.group(5)+"-"+m.group(6)); }
            }
            return vo;
        } catch (Exception e){return null;}
    }

    private String call(String fileKey){
        try{
            String url=ossPrivateFileService.generatePreviewUrl(fileKey,600);
            RecognizeIdcardRequest request=RecognizeIdcardRequest.builder().url(url).outputQualityInfo(true).outputFigure(false).build();
            RecognizeIdcardResponse response=aliyunOcrAsyncClient.recognizeIdcard(request).get();
            return response.getBody()==null?null:response.getBody().getData();
        } catch (InterruptedException e){Thread.currentThread().interrupt(); log.warn("ocr interrupted fileKey={}",fileKey);}
        catch (ExecutionException e){log.warn("ocr execution failed fileKey={}",fileKey);} catch (Exception e){log.warn("ocr failed fileKey={}",fileKey);} 
        return null;
    }
    private String text(JsonNode n,String f){JsonNode v=n.path(f);return v.isMissingNode()||v.isNull()?null:v.asText();}
    private String maskAddress(String s){ if(!StringUtils.hasText(s)||s.length()<6) return s; return s.substring(0,Math.min(4,s.length()))+"********"; }
}
