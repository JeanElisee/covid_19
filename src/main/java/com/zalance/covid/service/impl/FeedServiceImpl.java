package com.zalance.covid.service.impl;

import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.constant.Status;
import com.zalance.covid.convertor.CovidConvertor;
import com.zalance.covid.domain.ApiCallHistory;
import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.GlobalCases;
import com.zalance.covid.dto.*;
import com.zalance.covid.exception.CovidException;
import com.zalance.covid.repository.ApiCallHistoryRepository;
import com.zalance.covid.repository.GlobalCasesRepository;
import com.zalance.covid.service.CountryService;
import com.zalance.covid.service.CovidCasesService;
import com.zalance.covid.service.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
public class FeedServiceImpl implements FeedService {
    Logger logger = LoggerFactory.getLogger(FeedServiceImpl.class);

    @Value("${country.url}")
    private String countryUrl;
    @Value("${covid-cases.url}")
    private String covidCasesUrl;
    @Value("${covid-cases.daily.url}")
    private String dailyUpdateUrl;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${zalance.covid.country.queue.name}")
    private String covidCountryQueueName;
    @Value("${zalance.covid.cases.queue.name}")
    private String covidCasesQueueName;

    @Autowired
    private GlobalCasesRepository globalCasesRepository;
    @Autowired
    private ApiCallHistoryRepository apiCallHistoryRepository;

    private final CountryService countryService;
    private final CovidCasesService covidCasesService;

    public FeedServiceImpl(CountryService countryService, CovidCasesService covidCasesService) {
        this.countryService = countryService;
        this.covidCasesService = covidCasesService;
    }

    @Override
    public void getDataFromApi(ApiCallType from) {
        XyzVo xyzVo = null;
        try {
            xyzVo = covidDataSummaryClient();
            apiCallHistoryRepository.save(new ApiCallHistory(Status.SUCCESS.name(), new Date(), ApiCallType.CASES_SUMMARY.name()));
        } catch (Exception exception) {
            logger.info("An error occurred in covid case client call : {}", exception.toString());

            apiCallHistoryRepository.save(new ApiCallHistory(Status.FAILED.name(), new Date(), ApiCallType.CASES_SUMMARY.name()));

            if (!from.equals(ApiCallType.RETRY)) {
                ApiRetryVo apiRetryVo = new ApiRetryVo(ApiCallType.CASES_SUMMARY, Status.FAILED, new Date());
                rabbitTemplate.convertAndSend(covidCasesQueueName, apiRetryVo);
            }

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An error occured while fetching the cases from the API");
        }

        if (xyzVo == null || xyzVo.getCases() == null) {
            logger.info("No COVID cases found {}", new Date());
            return;
        }

        try {
            GlobalCases globalCases = CovidConvertor.INSTANCE.convertToGlobalCases(xyzVo.getGlobal(), true);
            globalCases.setCaseDate(xyzVo.getCasesDate());
            globalCases.setCountry(countryService.getCountryByIso("XX"));
            globalCasesRepository.save(globalCases);
            logger.info("Global case saved : {}", xyzVo.getGlobal().toString());
        } catch (Exception e) {
            logger.info("An error occurred while saving the global case detail", e);
        }

        for (GlobalCasesVo casesVo : xyzVo.getCases()) {
            if (casesVo.getCountryCode().equals(""))
                return;

            logger.info(casesVo.toString());
            Country country = new Country();

            try {
                country = countryService.getCountryByIso(casesVo.getCountryCode());
            } catch (DataAccessException dataAccessException) {
                return;
            } catch (CovidException covidException) {
                logger.info("No country found for {}", casesVo.getCountryCode());
                return;
            }

            try {
                covidCasesService.getCasesByDateAndCountry(casesVo);
                logger.info("Case already saved for {} on {} skipping...", casesVo.getCountryName(), casesVo.getCaseDate());
            } catch (Exception e) {
                logger.info("Saving new case {}", casesVo.toString());
                covidCasesService.saveCase(CovidConvertor.INSTANCE.convertToGlobalCases(casesVo, country));
            }
        }
    }

    @Override
    public void getCountryAndSave(ApiCallType from) {
        List<CountryDataDto> countryDataDtos = null;
        try {
            countryDataDtos = countryClient();

            apiCallHistoryRepository.save(new ApiCallHistory(Status.SUCCESS.name(), new Date(), ApiCallType.COUNTRY.name()));
        } catch (Exception exception) {
            logger.info("An error occurred in country client call : {}", exception.toString());

            apiCallHistoryRepository.save(new ApiCallHistory(Status.FAILED.name(), new Date(), ApiCallType.COUNTRY.name()));

            if (!from.equals(ApiCallType.RETRY)) {
                ApiRetryVo apiRetryVo = new ApiRetryVo(ApiCallType.COUNTRY, Status.FAILED, new Date());
                rabbitTemplate.convertAndSend(covidCountryQueueName, apiRetryVo);
            }

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An error occurred while fetching the countries from the API");
        }

        if (countryDataDtos == null) {
            logger.info("No country found {}", new Date());
            return;
        }

        // Adding a record for Global
        CountryDataDto countryDataDto = new CountryDataDto("Global", "Global", "XX");
        countryDataDtos.add(countryDataDto);

        logger.info("Countries received from API call {}", countryDataDtos);

        for (CountryDataDto c : countryDataDtos) {
            if (c.getIso().equals(""))
                return;
            try {
                countryService.getCountryByIso(c.getIso());
                logger.info("Country: {} already existing skipping...", c.toString());
            } catch (Exception e) {
                logger.info("Country: {} not existing saving...", c.toString());
                countryService.addCountry(CovidConvertor.INSTANCE.convertToCountry(c));
            }
        }
    }

    private List<CountryDataDto> countryClient() {
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<List<CountryDataDto>> dataReceivedFromApi = restTemplate.exchange(
                countryUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CountryDataDto>>() {
                });
        return dataReceivedFromApi.getBody();
    }

    private XyzVo covidDataSummaryClient() {
        final RestTemplate restTemplate = new RestTemplate();
        final ResponseEntity<XyzVo> xyzVo = restTemplate.exchange(
                dailyUpdateUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<XyzVo>() {
                });
        return xyzVo.getBody();
    }
}
