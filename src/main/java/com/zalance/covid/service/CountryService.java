package com.zalance.covid.service;

import com.zalance.covid.domain.Country;
import com.zalance.covid.exception.CovidException;

import java.util.List;

public interface CountryService {
    Country addCountry(Country country);

    List<Country> getCountries();

    Country getCountryByIso(String isoCode) throws CovidException;

    Country getCountryByIsoAndCity(String isoCode, String city) throws CovidException;
}
