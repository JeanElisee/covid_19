package com.zalance.covid.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EntryDataDto {
    @JsonProperty("Country")
    private String country;
    @JsonProperty("CountryCode")
    private String countryCode;
    @JsonProperty("Province")
    private String province;
    @JsonProperty("City")
    private String city;
    @JsonProperty("CityCode")
    private String cityCode;
    @JsonProperty("Lat")
    private String lat;
    @JsonProperty("Long")
    private String lon;
    @JsonProperty("Confirmed")
    private Long confirmed;
    @JsonProperty("Deaths")
    private Long deaths;
    @JsonProperty("Recovered")
    private Long recovered;
    @JsonProperty("Active")
    private Long active;
    @JsonProperty("Date")
    private Date date;
}
