package com.zalance.covid.service.impl;

import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.constant.Status;
import com.zalance.covid.convertor.CovidConvertor;
import com.zalance.covid.domain.ApiCallHistory;
import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.CovidCases;
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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
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
    public void getDataFromApi() {
        List<GlobalCasesVo> covidCases = null;
        GlobalCasesVo globalCasesVo = null;
        try {
            XyzVo xyzVo = covidDataSummaryClient();
            covidCases = xyzVo.getCases();
            globalCasesVo = xyzVo.getGlobal();

            apiCallHistoryRepository.save(new ApiCallHistory(Status.SUCCESS, new Date(), ApiCallType.CASES_SUMMARY));
        } catch (Exception exception) {
            logger.info("An error occurred in covid case client call : {}", exception.toString());

            apiCallHistoryRepository.save(new ApiCallHistory(Status.FAILED, new Date(), ApiCallType.CASES_SUMMARY));

            ApiRetryVo apiRetryVo = new ApiRetryVo(ApiCallType.CASES_SUMMARY, Status.FAILED, new Date());
            rabbitTemplate.convertAndSend(covidCasesQueueName, apiRetryVo);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An error occured while fetching the cases from the API");
        }

        if (covidCases == null) {
            logger.info("No COVID cases found {}", new Date());
            return;
        }

        globalCasesRepository.save(CovidConvertor.INSTANCE.convertToGlobalCases(globalCasesVo, true));

        for (GlobalCasesVo casesVo : covidCases) {
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
//                if (casesVo.getCity().equals("")) {
                    covidCasesService.getCasesByDateAndCountry(c);
//                } else {
//                    covidCasesService.getCasesByDateAndCountryAndCity(c);
//                }
                logger.info("Case already saved for {} on {} skipping...", c.getCountry(), c.getDate());
            } catch (Exception e) {
                logger.info("Saving new case {}", c.toString());
                covidCasesService.saveCase(CovidConvertor.INSTANCE.convertToCovidCases(c, country));
            }
        }
    }

    @Override
    public void getCountryAndSave() {
        List<CountryDataDto> countryDataDtos = null;
        try {
            countryDataDtos = countryClient();

            apiCallHistoryRepository.save(new ApiCallHistory(Status.SUCCESS, new Date(), ApiCallType.COUNTRY));
        } catch (Exception exception) {
            logger.info("An error occurred in country client call : {}", exception.toString());

            apiCallHistoryRepository.save(new ApiCallHistory(Status.FAILED, new Date(), ApiCallType.COUNTRY));

            ApiRetryVo apiRetryVo = new ApiRetryVo(ApiCallType.COUNTRY, Status.FAILED, new Date());
            rabbitTemplate.convertAndSend(covidCountryQueueName, apiRetryVo);

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An error occurred while fetching the countries from the API");
        }

        if (countryDataDtos == null) {
            logger.info("No country found {}", new Date());
            return;
        }

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

//        JsonArray obj = new JsonParser().parse(dataReceivedFromApi).getAsJsonArray();
//        List<CountryDataDto> countryDataDtos = new ArrayList<>();
//        Gson gson = new Gson();
//        for (JsonElement j : obj) {
//            countryDataDtos.add(gson.fromJson(j, CountryDataDto.class));
//        }
        return dataReceivedFromApi.getBody();
    }

    private List<EntryDataDto> covidDataClient() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        final RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<EntryDataDto>> entryDataDtos = restTemplate.exchange(
                covidCasesUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<EntryDataDto>>() {
                });

//        JsonArray obj = new JsonParser().parse(dataReceivedFromApi).getAsJsonArray();
//        List<EntryDataDto> entryDataDtos = new ArrayList<>();
//        Gson gson = new Gson();
//        for (JsonElement j : obj) {
//            entryDataDtos.add(gson.fromJson(j, EntryDataDto.class));
//        }
        return entryDataDtos.getBody();
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
