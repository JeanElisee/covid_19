package com.zalance.covid.service.impl;

import com.zalance.covid.domain.Country;
import com.zalance.covid.exception.CovidException;
import com.zalance.covid.exception.NotFoundException;
import com.zalance.covid.repository.CountryRepository;
import com.zalance.covid.service.CountryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CountryServiceImpl implements CountryService {
    Logger logger = LoggerFactory.getLogger(CountryServiceImpl.class);

    private final CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Override
    public Country addCountry(Country country) {
        try {
            return countryRepository.save(country);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while saving the country: {}, The exception is {}", country.toString(), dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        } catch (Exception e) {
            logger.warn("Error while saving the country: {}, The exception is {}", country.toString(), e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The country couldn't be saved");
        }
    }

    @Override
    public Country getCountryByIso(String isoCode) throws CovidException, NotFoundException {
        Country country = null;
        try {
            country = countryRepository.findByIso(isoCode);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching the country by ISO code {}: {}", isoCode, dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        } catch (Exception e) {
            logger.warn("Error while fetching the country by ISO code {}: {}", isoCode, e.toString());
            throw new CovidException("Error while fetching the country", e.getCause());
        }

        if (country == null) {
            throw new NotFoundException("No country found for this ISO code");
        }

        return country;
    }

    @Override
    public Country getCountryByIsoAndCity(String isoCode, String city) throws CovidException, NotFoundException {
        Country country = null;
        try {
            country = getCountryByIso(isoCode);
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching the country by ISO code {}: {}", isoCode, dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        } catch (Exception e) {
            logger.warn("Error while fetching the country by ISO code {}: {}", isoCode, e.toString());
            throw new CovidException("Error while fetching the country", e.getCause());
        }

        if (country == null) {
            throw new NotFoundException("No country found for this ISO code");
        }

        return country;
    }

    @Override
    public List<Country> getCountries() throws NotFoundException {
        List<Country> countries;
        try {
            countries = countryRepository.findAll();
        } catch (DataAccessException dataAccessException) {
            logger.warn("Error while fetching all the countries {}", dataAccessException.toString());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred");
        } catch (Exception e) {
            logger.warn("Error while fetching the countries {}", e.toString());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error while fetching the country");
        }

        if (countries.isEmpty()) {
            throw new NotFoundException("No country found in the database");
        }

        return countries;
    }
}
