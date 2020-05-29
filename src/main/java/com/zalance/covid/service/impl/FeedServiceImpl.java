package com.zalance.covid.service.impl;

import com.zalance.covid.client.Covid19Api;
import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.constant.ErrorCode;
import com.zalance.covid.constant.Status;
import com.zalance.covid.convertor.CovidConvertor;
import com.zalance.covid.domain.ApiCallHistory;
import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.GlobalCases;
import com.zalance.covid.dto.CountryDataDto;
import com.zalance.covid.dto.GlobalCasesDto;
import com.zalance.covid.dto.XyzDto;
import com.zalance.covid.exception.CovidException;
import com.zalance.covid.exception.NotFoundException;
import com.zalance.covid.exception.RetryException;
import com.zalance.covid.repository.ApiCallHistoryRepository;
import com.zalance.covid.service.CountryService;
import com.zalance.covid.service.CovidCasesService;
import com.zalance.covid.service.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class FeedServiceImpl implements FeedService {
    Logger logger = LoggerFactory.getLogger(FeedServiceImpl.class);

    private final ApiCallHistoryRepository apiCallHistoryRepository;
    private final Covid19Api covid19Api;
    
    private final CountryService countryService;
    private final CovidCasesService covidCasesService;

    public FeedServiceImpl(CountryService countryService, CovidCasesService covidCasesService, ApiCallHistoryRepository apiCallHistoryRepository, Covid19Api covid19Api) {
        this.countryService = countryService;
        this.covidCasesService = covidCasesService;
        this.apiCallHistoryRepository = apiCallHistoryRepository;
        this.covid19Api = covid19Api;
    }

    @Override
    public void getDataFromApi(ApiCallType from) throws RetryException, CovidException, NotFoundException {
        XyzDto xyzDto = null;
        ApiCallHistory apiCallHistory = null;
        try {
            xyzDto = covid19Api.covidDataSummaryClient();

            if (xyzDto == null || xyzDto.getCases() == null) {
                logger.info("No COVID cases found {}", new Date());
                return;
            }

            xyzDto.getGlobal().setDate(xyzDto.getCasesDate());
            xyzDto.getGlobal().setCountryCode("XX");
            xyzDto.getGlobal().setGlobal(true);
            xyzDto.getCases().add(xyzDto.getGlobal());

            apiCallHistory = apiCallHistoryRepository.save(new ApiCallHistory(Status.SUCCESS.name(), new Date(), ApiCallType.CASES_SUMMARY.name(), (long) xyzDto.getCases().size()));
        } catch (Exception exception) {
            logger.info("An error occurred in covid case client call : {}", exception.toString());

            apiCallHistoryRepository.save(new ApiCallHistory(Status.FAILED.name(), new Date(), ApiCallType.CASES_SUMMARY.name()));

            if (!from.equals(ApiCallType.RETRY)) {
                throw new RetryException(exception.getMessage(), ErrorCode.NEED_TO_RETRY.getFieldValue());
            }

            throw new RetryException("An error occurred while fetching the cases from the API", ErrorCode.ANOTHER_ATTEMPT.getFieldValue());
        }

        logger.info("Summary is {}", xyzDto.toString());

        long saved = 0L;
        long updated = 0L;
        long noChange = 0L;

        for (GlobalCasesDto casesVo : xyzDto.getCases()) {
            if (casesVo == null)
                return;
            if (casesVo.getCountryCode().equals(""))
                return;

            Country country = new Country();

            try {
                country = countryService.getCountryByIso(casesVo.getCountryCode());
            } catch (DataAccessException dataAccessException) {
                continue;
            } catch (NotFoundException notFoundException) {
                logger.info("No country found for '{}' -> '{}'", casesVo.getCountryCode(), casesVo.getCountryName());
                continue;
            } catch (Exception exception) {
                logger.info("An exception occurred {}", exception.toString());
                continue;
            }

            try {
                GlobalCases globalCasesRetrieved = covidCasesService.getCasesByDateAndCountry(casesVo);

                if (globalCasesRetrieved != null &&
                        (!globalCasesRetrieved.getNewConfirmed().equals(casesVo.getNewConfirmed()) ||
                                !globalCasesRetrieved.getNewDeaths().equals(casesVo.getNewDeaths()) ||
                                !globalCasesRetrieved.getNewRecovered().equals(casesVo.getNewRecovered()) ||
                                !globalCasesRetrieved.getNewConfirmed().equals(casesVo.getNewConfirmed()) ||
                                !globalCasesRetrieved.getTotalConfirmed().equals(casesVo.getTotalConfirmed()) ||
                                !globalCasesRetrieved.getTotalDeaths().equals(casesVo.getTotalDeaths()) ||
                                !globalCasesRetrieved.getTotalRecovered().equals(casesVo.getTotalRecovered()))) {
                    updated += 1;
                    logger.info("Updating case : {} with : {}", globalCasesRetrieved.getId(), casesVo.toString());
                    covidCasesService.saveCase(CovidConvertor.INSTANCE.convertToGlobalCases(casesVo, globalCasesRetrieved, country));
                } else if (globalCasesRetrieved == null) {
                    saved += 1;
                    logger.info("Saving new case : {}", casesVo.toString());
                    covidCasesService.saveCase(CovidConvertor.INSTANCE.convertToGlobalCases(casesVo, country));
                } else {
                    noChange += 1;
                    logger.info("Case already saved for: {}, skipping...", casesVo.toString());
                }
            } catch (Exception e) {
                logger.info("An error occurred while checking if the case already exist in DB : {}, {}", casesVo.toString(), e);
            }
        }

        apiCallHistory.setNoChange(noChange);
        apiCallHistory.setUpdated(updated);
        apiCallHistory.setSaved(saved);
        apiCallHistoryRepository.save(apiCallHistory);
    }

    @Override
    public void getCountryAndSave(ApiCallType from) throws RetryException {
        List<CountryDataDto> countryDataDtos = null;
        try {
            countryDataDtos = covid19Api.countryClient();

            if (countryDataDtos == null) {
                logger.info("No country found, {}. Another call planned for : {}", new Date(), LocalDateTime.from((new Date()).toInstant()).plusDays(3));
                return;
            }

            apiCallHistoryRepository.save(new ApiCallHistory(Status.SUCCESS.name(), new Date(), ApiCallType.COUNTRY.name(), (long) countryDataDtos.size()));
        } catch (Exception exception) {
            logger.info("An error occurred in country client call : {}", exception.toString());
            apiCallHistoryRepository.save(new ApiCallHistory(Status.FAILED.name(), new Date(), ApiCallType.COUNTRY.name()));

            if (!from.equals(ApiCallType.RETRY)) {
                throw new RetryException(exception.getMessage(), ErrorCode.NEED_TO_RETRY.getFieldValue());
            }

            throw new RetryException("An error occurred while fetching the countries from the API", ErrorCode.ANOTHER_ATTEMPT.getFieldValue());
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
            } catch (NotFoundException notFoundException) {
                logger.info("Country: {} not existing saving...", c.toString());
                countryService.addCountry(CovidConvertor.INSTANCE.convertToCountry(c));
            } catch (Exception e) {
                logger.info("An error occured {}", e.toString());
            }
        }
    }
}
