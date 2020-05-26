package com.zalance.covid.service;

import com.zalance.covid.domain.CovidCases;
import com.zalance.covid.dto.EntryDataDto;
import com.zalance.covid.exception.CovidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CovidCasesService {
    List<CovidCases> saveCases(List<CovidCases> covidCases);

    CovidCases saveCase(CovidCases covidCases);

    Page<CovidCases> getCases(Pageable pageable);

    Page<CovidCases> getCasesByCountry(EntryDataDto entryDataDto, Pageable pageable) throws CovidException;

    Page<CovidCases> getCasesByDate(EntryDataDto entryDataDto, Pageable pageable);

    List<CovidCases> getCasesByDateAndCountry(EntryDataDto entryDataDto) throws CovidException;

    List<CovidCases> getCasesByDateAndCountryAndCity(EntryDataDto entryDataDto) throws CovidException;
}
