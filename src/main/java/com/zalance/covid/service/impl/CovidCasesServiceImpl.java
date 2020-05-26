package com.zalance.covid.service.impl;

import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.GlobalCases;
import com.zalance.covid.dto.EntryDataDto;
import com.zalance.covid.dto.GlobalCasesVo;
import com.zalance.covid.exception.CovidException;
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

    private GlobalCasesRepository globalCasesRepository;

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
    public GlobalCases saveCase(GlobalCases globalCases) {
        try {
            return globalCasesRepository.save(globalCases);
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
            logger.warn("No cases found in get all the cases.");
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cases found");
        }

        return covidCases;
    }

    @Override
    public Page<GlobalCases> getCasesByCountry(EntryDataDto entryDataDto, Pageable pageable) throws CovidException {
        Country country = countryService.getCountryByIso(entryDataDto.getCountryCode());
        Page<GlobalCases> covidCases;

        try {
            covidCases = globalCasesRepository.findAllByCountryOrderByCaseDateDesc(country, pageable);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching the cases by country {}: {}", entryDataDto.getCountryCode(), dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cases found");
        } catch (Exception e) {
            logger.warn("Error while fetching the cases by country {}: {}", entryDataDto.getCountryCode(), e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cases found");
        }

        if (covidCases.isEmpty()) {
            logger.warn("No cases found for the country {}", entryDataDto.getCountryCode());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cases found for the country " + entryDataDto.getCountryCode());
        }

        return covidCases;
    }

    @Override
    public Page<GlobalCases> getCasesByDate(GlobalCasesVo globalCasesVo, Pageable pageable) {
        Page<GlobalCases> covidCases;
        try {
            covidCases = globalCasesRepository.findAllByCaseDateOrderByCaseDateDesc(globalCasesVo.getCaseDate(), pageable);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching the cases by date {}", dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cases found");
        } catch (Exception e) {
            logger.warn("Error while fetching the cases by date {}", e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cases found");
        }

        if (covidCases.isEmpty()) {
            logger.warn("No cases found for the date {}", globalCasesVo.getCountryCode());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cases found for the date " + globalCasesVo.getCountryCode());
        }

        return covidCases;
    }

    @Override
    public List<GlobalCases> getCasesByDateAndCountry(GlobalCasesVo globalCasesVo) throws CovidException {
        Country country = countryService.getCountryByIso(globalCasesVo.getCountryCode());
        List<GlobalCases> covidCases;

        try {
            covidCases = globalCasesRepository.findAllByCountryAndCaseDateAndNewConfirmedAndNewDeathsAndNewRecoveredAndTotalConfirmedOrderByCaseDateDesc(country, globalCasesVo.getCaseDate(), globalCasesVo.getNewConfirmed(), globalCasesVo.getNewDeaths(), globalCasesVo.getNewRecovered(), globalCasesVo.getTotalConfirmed());
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching the cases by country {} and date {} : {}", globalCasesVo.getCountryCode(), globalCasesVo.getCaseDate(), dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cases found");
        } catch (Exception e) {
            logger.warn("Error while fetching the cases by country and date {}", e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cases found");
        }

        if (covidCases.isEmpty()) {
            logger.warn("No cases found for {} -> {} on {}", globalCasesVo.getCountryName(), globalCasesVo.getCountryCode(), globalCasesVo.getCaseDate());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cases found for " + globalCasesVo.getCountryName() + " -> " + globalCasesVo.getCountryCode() + " on " + globalCasesVo.getCaseDate());
        }

        return covidCases;
    }

//    @Override
//    public List<GlobalCases> getCasesByDateAndCountryAndCity(EntryDataDto entryDataDto) throws CovidException {
//        Country country = countryService.getCountryByIso(entryDataDto.getCountryCode());
//        List<GlobalCases> covidCases;
//
//        try {
//            covidCases = globalCasesRepository.findAllByCountryAndDateAndCityOrderByDateDesc(country, entryDataDto.getDate(), entryDataDto.getCity());
//        } catch (DataAccessException dataAccessException) {
//            logger.warn("Error while fetching the cases by country {} and date {} : {}", entryDataDto.getCountryCode(), entryDataDto.getDate(), dataAccessException.toString());
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cases found");
//        } catch (Exception e) {
//            logger.warn("Error while fetching the cases by country and date {}", e.toString());
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cases found");
//        }
//
//        if (covidCases.isEmpty()) {
//            logger.warn("No cases found for {} on {}", entryDataDto.getCountryCode(), entryDataDto.getDate());
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cases found for " + entryDataDto.getCountry() + " -> " + entryDataDto.getCountryCode() + " on " + entryDataDto.getDate());
//        }
//
//        return covidCases;
//    }
}
