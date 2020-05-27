package com.zalance.covid.service;

import com.zalance.covid.domain.Country;
import com.zalance.covid.exception.CovidException;
import com.zalance.covid.exception.NotFoundException;

import java.util.List;

public interface CountryService {
    Country addCountry(Country country);

    List<Country> getCountries() throws NotFoundException;

    Country getCountryByIso(String isoCode) throws CovidException, NotFoundException;

    Country getCountryByIsoAndCity(String isoCode, String city) throws CovidException, NotFoundException;
}
