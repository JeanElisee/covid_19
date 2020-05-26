package com.zalance.covid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class GlobalCasesVo {
    @JsonProperty("Country")
    private String countryName;
    @JsonProperty("CountryCode")
    private String countryCode;
    @JsonProperty("Slug")
    private String countryCommonName;

    @JsonProperty("NewConfirmed")
    private Long newConfirmed;
    @JsonProperty("TotalConfirmed")
    private Long totalConfirmed;
    @JsonProperty("NewDeaths")
    private Long newDeaths;
    @JsonProperty("TotalDeaths")
    private Long totalDeaths;
    @JsonProperty("NewRecovered")
    private Long newRecovered;
    @JsonProperty("TotalRecovered")
    private Long totalRecovered;

    @JsonProperty("Date")
    private Date date;
}
