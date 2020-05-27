package com.zalance.covid.dto;

import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.constant.Status;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
public class ApiRetryDto implements Serializable {
    private ApiCallType apiCallType;
    private Status status;
    private Date failureDateTime;
    private String reason;

    public ApiRetryDto() {
    }

//    public ApiRetryVo(ApiCallType apiCallType, Status status, Date failureDateTime) {
//        this.apiCallType = apiCallType;
//        this.status = status;
//        this.failureDateTime = failureDateTime;
//    }

    public ApiRetryDto(ApiCallType apiCallType, Status status, Date failureDateTime, String reason) {
        this.apiCallType = apiCallType;
        this.status = status;
        this.failureDateTime = failureDateTime;
        this.reason = reason;
    }
}
