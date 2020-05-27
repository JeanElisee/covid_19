package com.zalance.covid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class XyzDto {
    @JsonProperty("Global")
    private GlobalCasesDto global;
    @JsonProperty("Countries")
    private List<GlobalCasesDto> cases;
    @JsonProperty("Date")
    private Date casesDate;
}
