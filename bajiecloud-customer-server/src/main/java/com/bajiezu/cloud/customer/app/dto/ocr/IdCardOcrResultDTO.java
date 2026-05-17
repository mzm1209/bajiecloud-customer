package com.bajiezu.cloud.customer.app.dto.ocr;

import lombok.Data;

@Data
public class IdCardOcrResultDTO {
    private String side;

    private String name;
    private String sex;
    private String ethnicity;
    private String birthDate;
    private String address;
    private String idNumber;

    private String issueAuthority;
    private String validPeriod;
    private String validStartDate;
    private String validEndDate;

    private Boolean success;
    private String rawData;
    private String requestId;
    private String errorMessage;
}
