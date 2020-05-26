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
public class ApiRetryVo implements Serializable {
    private ApiCallType apiCallType;
    private Status status;
    private Date failureDateTime;

    public ApiRetryVo() {
    }

    public ApiRetryVo(ApiCallType apiCallType, Status status, Date failureDateTime) {
        this.apiCallType = apiCallType;
        this.status = status;
        this.failureDateTime = failureDateTime;
    }
}
