package com.zalance.covid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zalance.covid.domain.GlobalCases;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class XyzVo {
    @JsonProperty("Global")
    private GlobalCasesVo global;
    @JsonProperty("Countries")
    private List<GlobalCasesVo> cases;
}
