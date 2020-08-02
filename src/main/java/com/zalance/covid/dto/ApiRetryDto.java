package com.zalance.covid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.constant.Status;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ApiRetryDto implements Serializable {
    @JsonProperty("apiCallType")
    private ApiCallType apiCallType;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("failureDateTime")
    private LocalDateTime failureDateTime;
    @JsonProperty("reason")
    private String reason;
}
