package com.zalance.covid.service;

import com.zalance.covid.domain.GlobalCases;
import com.zalance.covid.dto.EntryDataDto;
import com.zalance.covid.dto.GlobalCasesDto;
import com.zalance.covid.exception.CovidException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CovidCasesService {
    List<GlobalCases> saveCases(List<GlobalCases> covidCases);

    GlobalCases saveCase(GlobalCases globalCases);

    Page<GlobalCases> getCases(Pageable pageable);

    Page<GlobalCases> getCasesByCountry(EntryDataDto entryDataDto, Pageable pageable) throws CovidException;

    Page<GlobalCases> getCasesByDate(GlobalCasesDto globalCasesDto, Pageable pageable);

    List<GlobalCases> getCasesByDateAndCountry(GlobalCasesDto globalCasesDto) throws CovidException;

//    List<GlobalCases> getCasesByDateAndCountryAndCity(EntryDataDto entryDataDto) throws CovidException;
}
