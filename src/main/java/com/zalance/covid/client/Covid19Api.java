package com.zalance.covid.client;

import com.zalance.covid.dto.CountryDataDto;
import com.zalance.covid.dto.XyzDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class Covid19Api {
    @Value("${country.url}")
    private String countryUrl;
    @Value("${covid-cases.daily.url}")
    private String dailyUpdateUrl;

    public List<CountryDataDto> countryClient() {
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<List<CountryDataDto>> dataReceivedFromApi = restTemplate.exchange(
                countryUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CountryDataDto>>() {
                });
        return dataReceivedFromApi.getBody();
    }

    public XyzDto covidDataSummaryClient() {
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<XyzDto> xyzVo = restTemplate.exchange(
                dailyUpdateUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<XyzDto>() {
                });
        return xyzVo.getBody();
    }
}
