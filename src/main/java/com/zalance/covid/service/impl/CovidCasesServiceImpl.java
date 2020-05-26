package com.zalance.covid.service.impl;

import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.CovidCases;
import com.zalance.covid.dto.EntryDataDto;
import com.zalance.covid.exception.CovidException;
import com.zalance.covid.repository.CovidCasesRepository;
import com.zalance.covid.service.CountryService;
import com.zalance.covid.service.CovidCasesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final CovidCasesRepository covidCasesRepository;
    private final CountryService countryService;

    public CovidCasesServiceImpl(CovidCasesRepository covidCasesRepository, CountryService countryService) {
        this.covidCasesRepository = covidCasesRepository;
        this.countryService = countryService;
    }

    @Override
    public List<CovidCases> saveCases(List<CovidCases> covidCases) {
        if (covidCases.isEmpty()) {
            logger.warn("List of cases is empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Couldn't save the list of cases.");
        }

        try {
            return covidCasesRepository.saveAll(covidCases);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while saving the cases: {}", dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        } catch (Exception e) {
            logger.warn("Error while saving all the cases {}", e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The cases couldn't be saved.");
        }
    }

    @Override
    public CovidCases saveCase(CovidCases covidCases) {
        try {
            return covidCasesRepository.save(covidCases);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while saving the case {}: {}", covidCases.toString(), dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        } catch (Exception e) {
            logger.warn("Error while saving the case {} {}", covidCases.toString(), e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The case couldn't be saved.");
        }
    }

    @Override
    public Page<CovidCases> getCases(Pageable pageable) {
        Page<CovidCases> covidCases;
        try {
            covidCases = covidCasesRepository.findAll(pageable);
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
    public Page<CovidCases> getCasesByCountry(EntryDataDto entryDataDto, Pageable pageable) throws CovidException {
        Country country = countryService.getCountryByIso(entryDataDto.getCountryCode());
        Page<CovidCases> covidCases;

        try {
            covidCases = covidCasesRepository.findAllByCountryOrderByDateDesc(country, pageable);
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
    public Page<CovidCases> getCasesByDate(EntryDataDto entryDataDto, Pageable pageable) {
        Page<CovidCases> covidCases;
        try {
            covidCases = covidCasesRepository.findAllByDateOrderByDateDesc(entryDataDto.getDate(), pageable);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching the cases by date {}", dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cases found");
        } catch (Exception e) {
            logger.warn("Error while fetching the cases by date {}", e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cases found");
        }

        if (covidCases.isEmpty()) {
            logger.warn("No cases found for the date {}", entryDataDto.getCountryCode());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cases found for the date " + entryDataDto.getCountryCode());
        }

        return covidCases;
    }

    @Override
    public List<CovidCases> getCasesByDateAndCountry(EntryDataDto entryDataDto) throws CovidException {
        Country country = countryService.getCountryByIso(entryDataDto.getCountryCode());
        List<CovidCases> covidCases;

        try {
            covidCases = covidCasesRepository.findAllByCountryAndDateOrderByDateDesc(country, entryDataDto.getDate());
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching the cases by country {} and date {} : {}", entryDataDto.getCountryCode(), entryDataDto.getDate(), dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cases found");
        } catch (Exception e) {
            logger.warn("Error while fetching the cases by country and date {}", e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cases found");
        }

        if (covidCases.isEmpty()) {
            logger.warn("No cases found for {} -> {} on {}", entryDataDto.getCountry(), entryDataDto.getCountryCode(), entryDataDto.getDate());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cases found for " + entryDataDto.getCountry() + " -> " + entryDataDto.getCountryCode() + " on " + entryDataDto.getDate());
        }

        return covidCases;
    }

    @Override
    public List<CovidCases> getCasesByDateAndCountryAndCity(EntryDataDto entryDataDto) throws CovidException {
        Country country = countryService.getCountryByIso(entryDataDto.getCountryCode());
        List<CovidCases> covidCases;

        try {
            covidCases = covidCasesRepository.findAllByCountryAndDateAndCityOrderByDateDesc(country, entryDataDto.getDate(), entryDataDto.getCity());
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching the cases by country {} and date {} : {}", entryDataDto.getCountryCode(), entryDataDto.getDate(), dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No cases found");
        } catch (Exception e) {
            logger.warn("Error while fetching the cases by country and date {}", e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No cases found");
        }

        if (covidCases.isEmpty()) {
            logger.warn("No cases found for {} on {}", entryDataDto.getCountryCode(), entryDataDto.getDate());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No cases found for " + entryDataDto.getCountry() + " -> " + entryDataDto.getCountryCode() + " on " + entryDataDto.getDate());
        }

        return covidCases;
    }
}
