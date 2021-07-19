package com.zalance.covid.service.impl;

import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.GlobalCases;
import com.zalance.covid.dto.GlobalCasesDto;
import com.zalance.covid.dto.SearchDto;
import com.zalance.covid.exception.CovidException;
import com.zalance.covid.exception.NotFoundException;
import com.zalance.covid.repository.GlobalCasesRepository;
import com.zalance.covid.service.CountryService;
import com.zalance.covid.service.CovidCasesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CovidCasesServiceImpl implements CovidCasesService {
    Logger logger = LoggerFactory.getLogger(CovidCasesServiceImpl.class);

    private final CountryService countryService;

    private final GlobalCasesRepository globalCasesRepository;

    @Autowired
    public CovidCasesServiceImpl(CountryService countryService, GlobalCasesRepository globalCasesRepository) {
        this.countryService = countryService;
        this.globalCasesRepository = globalCasesRepository;
    }

    @Override
    public List<GlobalCases> saveCases(List<GlobalCases> covidCases) {
        if (covidCases.isEmpty()) {
            logger.warn("List of cases is empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't save the list of cases.");
        }

        try {
            return globalCasesRepository.saveAll(covidCases);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while saving the cases: {}", dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        } catch (Exception e) {
            logger.warn("Error while saving all the cases {}", e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The cases couldn't be saved.");
        }
    }

    @Override
    public void saveCase(GlobalCases globalCases) {
        try {
            globalCasesRepository.save(globalCases);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while saving the case {}: {}", globalCases.toString(), dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        } catch (Exception e) {
            logger.warn("Error while saving the case {} {}", globalCases.toString(), e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The case couldn't be saved.");
        }
    }

    @Override
    public Page<GlobalCases> getCases(Pageable pageable) {
        Page<GlobalCases> covidCases;
        try {
            covidCases = globalCasesRepository.findAll(pageable);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching all the case: {}", dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        } catch (Exception e) {
            logger.warn("Error while fetching all the cases {}", e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The cases couldn't be fetched");
        }

        if (covidCases.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cases found");
        }

        return covidCases;
    }

    @Override
    public Page<GlobalCases> getCasesByCountry(SearchDto searchDto, Pageable pageable) throws CovidException, NotFoundException {
        Country country = countryService.getCountryByIso(searchDto.getCountryCode());
        Page<GlobalCases> covidCases;

        try {
            covidCases = globalCasesRepository.findAllByCountryOrderByCaseDateDesc(country, pageable);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching the cases by country {}: {}", searchDto.getCountryCode(), dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cases found");
        } catch (Exception e) {
            logger.warn("Error while fetching the cases by country {}: {}", searchDto.getCountryCode(), e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cases found");
        }

        if (covidCases.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cases found for the country " + searchDto.getCountryCode());
        }

        return covidCases;
    }

    @Override
    public Page<GlobalCases> getCasesByDate(SearchDto searchDto, Pageable pageable) {
        Page<GlobalCases> covidCases;
        try {
            covidCases = globalCasesRepository.findAllByCaseDateOrderByCaseDateDesc(searchDto.getDate(), pageable);
        } catch (DataAccessException dataAccessException) {
            logger.error("Error while fetching the cases by date {}", dataAccessException.toString(), dataAccessException);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cases found");
        } catch (Exception e) {
            logger.error("Error while fetching the cases by date {}", e.toString(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cases found");
        }

        if (covidCases.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cases found for the date " + searchDto.getCountryCode());
        }

        return covidCases;
    }

    @Override
    public List<GlobalCases> getCasesByDateAndCountryAndOtherDetails(GlobalCasesDto globalCasesDto) throws CovidException, NotFoundException {
        Country country = countryService.getCountryByIso(globalCasesDto.getCountryCode());
        List<GlobalCases> covidCases;

        try {
            covidCases = globalCasesRepository.findAllByCountryAndCaseDateAndNewConfirmedAndNewDeathsAndNewRecoveredAndTotalConfirmedAndTotalDeathsAndTotalRecovered(country, globalCasesDto.getDate().toLocalDate(), globalCasesDto.getNewConfirmed(), globalCasesDto.getNewDeaths(), globalCasesDto.getNewRecovered(), globalCasesDto.getTotalConfirmed(), globalCasesDto.getTotalDeaths(), globalCasesDto.getTotalRecovered());
        } catch (DataAccessException dataAccessException) {
            logger.error("Error while fetching the cases by country {} and date {} : {}", globalCasesDto.getCountryCode(), globalCasesDto.getDate(), dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cases found");
        } catch (Exception e) {
            logger.error("Error while fetching the cases by country and date {}", e.toString(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cases found");
        }

        if (covidCases.isEmpty()) {
            throw new NotFoundException("No cases found for " + globalCasesDto.getCountryName() + " -> " + globalCasesDto.getCountryCode() + " on " + globalCasesDto.getDate());
        }

        return covidCases;
    }

    @Override
    public GlobalCases getCasesByDateAndCountry(GlobalCasesDto globalCasesDto) throws CovidException, NotFoundException {
        Country country = countryService.getCountryByIso(globalCasesDto.getCountryCode());
        GlobalCases covidCases;

        try {
            covidCases = globalCasesRepository.findAllByCountryAndCaseDate(country, globalCasesDto.getDate().toLocalDate());
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching the cases by country {} and date {} : {}", globalCasesDto.getCountryCode(), globalCasesDto.getDate(), dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cases found");
        } catch (Exception e) {
            logger.warn("Error while fetching the cases by country and date {}", e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cases found");
        }
        return covidCases;
    }
}
