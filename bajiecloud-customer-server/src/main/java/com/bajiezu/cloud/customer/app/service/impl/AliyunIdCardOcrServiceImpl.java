package com.bajiezu.cloud.customer.app.service.impl;

import com.aliyun.sdk.service.ocr_api20210707.AsyncClient;
import com.aliyun.sdk.service.ocr_api20210707.models.RecognizeIdcardRequest;
import com.aliyun.sdk.service.ocr_api20210707.models.RecognizeIdcardResponse;
import com.bajiezu.cloud.customer.app.client.OssPrivateFileService;
import com.bajiezu.cloud.customer.app.dto.ocr.IdCardOcrResultDTO;
import com.bajiezu.cloud.customer.app.service.AliyunIdCardOcrService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class AliyunIdCardOcrServiceImpl implements AliyunIdCardOcrService {

    private static final Pattern VALID_PERIOD_PATTERN = Pattern.compile("(\\d{4})[.\\-/]?(\\d{2})[.\\-/]?(\\d{2}).*?(\\d{4})[.\\-/]?(\\d{2})[.\\-/]?(\\d{2})");

    @Resource
    private AsyncClient aliyunOcrAsyncClient;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private OssPrivateFileService ossPrivateFileService;

    @Override
    public IdCardOcrResultDTO recognizeByUrl(String imageUrl, String side) {
        IdCardOcrResultDTO result = new IdCardOcrResultDTO();
        String normalizedSide = normalizeSide(side);
        result.setSide(normalizedSide);

        if (!StringUtils.hasText(imageUrl)) {
            return fail(result, "imageUrl不能为空");
        }
        try {
            RecognizeIdcardRequest request = RecognizeIdcardRequest.builder()
                    .url(imageUrl)
                    .outputQualityInfo(true)
                    .outputFigure(false)
                    .build();
            RecognizeIdcardResponse response = aliyunOcrAsyncClient.recognizeIdcard(request).get();
            String requestId = response.getBody() == null ? null : response.getBody().getRequestId();
            String data = response.getBody() == null ? null : response.getBody().getData();
            result.setRequestId(requestId);
            result.setRawData(data);
            if (!StringUtils.hasText(data)) {
                return fail(result, "OCR返回Data为空");
            }
            parseData(result, data, normalizedSide);
            result.setSuccess(true);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("idcard ocr interrupted, side={}", normalizedSide);
            return fail(result, "OCR识别被中断");
        } catch (ExecutionException e) {
            log.warn("idcard ocr execution failed, side={}, error={}", normalizedSide, e.getClass().getSimpleName());
            return fail(result, "OCR识别失败");
        } catch (Exception e) {
            log.warn("idcard ocr failed, side={}, error={}", normalizedSide, e.getClass().getSimpleName());
            return fail(result, "OCR识别失败");
        }
    }

    @Override
    public IdCardOcrResultDTO recognizeByFileKey(String fileKey, String side) {
        if (!StringUtils.hasText(fileKey)) {
            IdCardOcrResultDTO result = new IdCardOcrResultDTO();
            result.setSide(normalizeSide(side));
            return fail(result, "fileKey不能为空");
        }
        String imageUrl = ossPrivateFileService.generatePreviewUrl(fileKey, 600);
        return recognizeByUrl(imageUrl, side);
    }

    private void parseData(IdCardOcrResultDTO result, String data, String side) throws Exception {
        JsonNode root = objectMapper.readTree(data);
        JsonNode sideNode = "FRONT".equals(side) ? root.path("face").path("data") : root.path("back").path("data");
        if (sideNode.isMissingNode() || sideNode.isNull()) {
            result.setErrorMessage("OCR返回结构不符合预期");
            return;
        }
        if ("FRONT".equals(side)) {
            result.setName(read(sideNode, "name"));
            result.setSex(read(sideNode, "sex"));
            result.setEthnicity(read(sideNode, "ethnicity"));
            result.setBirthDate(read(sideNode, "birthDate"));
            result.setAddress(read(sideNode, "address"));
            result.setIdNumber(read(sideNode, "idNumber"));
        } else {
            result.setIssueAuthority(read(sideNode, "issueAuthority"));
            String validPeriod = read(sideNode, "validPeriod");
            result.setValidPeriod(validPeriod);
            parseValidPeriod(result, validPeriod);
        }
    }

    private void parseValidPeriod(IdCardOcrResultDTO result, String validPeriod) {
        if (!StringUtils.hasText(validPeriod)) {
            return;
        }
        String normalized = validPeriod.replace("至", "-").trim();
        if ("长期".equals(normalized)) {
            result.setValidEndDate("长期");
            return;
        }
        Matcher matcher = VALID_PERIOD_PATTERN.matcher(normalized);
        if (matcher.find()) {
            result.setValidStartDate(matcher.group(1) + "-" + matcher.group(2) + "-" + matcher.group(3));
            result.setValidEndDate(matcher.group(4) + "-" + matcher.group(5) + "-" + matcher.group(6));
        }
    }

    private String read(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? null : value.asText();
    }

    private String normalizeSide(String side) {
        if (!StringUtils.hasText(side)) {
            throw new IllegalArgumentException("side不能为空");
        }
        String normalized = side.trim().toUpperCase(Locale.ROOT);
        if (!"FRONT".equals(normalized) && !"BACK".equals(normalized)) {
            throw new IllegalArgumentException("side只允许FRONT/BACK");
        }
        return normalized;
    }

    private IdCardOcrResultDTO fail(IdCardOcrResultDTO result, String message) {
        result.setSuccess(false);
        result.setErrorMessage(message);
        return result;
    }
}
