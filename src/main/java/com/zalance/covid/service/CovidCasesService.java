package com.zalance.covid.service;

import com.zalance.covid.domain.GlobalCases;
import com.zalance.covid.dto.GlobalCasesDto;
import com.zalance.covid.dto.SearchDto;
import com.zalance.covid.exception.CovidException;
import com.zalance.covid.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CovidCasesService {
    List<GlobalCases> saveCases(List<GlobalCases> covidCases);

    void saveCase(GlobalCases globalCases);

    Page<GlobalCases> getCases(Pageable pageable);

    Page<GlobalCases> getCasesByCountry(SearchDto searchDto, Pageable pageable) throws CovidException, NotFoundException;

    Page<GlobalCases> getCasesByDate(SearchDto searchDto, Pageable pageable);

    List<GlobalCases> getCasesByDateAndCountryAndOtherDetails(GlobalCasesDto globalCasesDto) throws CovidException, NotFoundException;

    GlobalCases getCasesByDateAndCountry(GlobalCasesDto globalCasesDto) throws CovidException, NotFoundException;

//    List<GlobalCases> getCasesByDateAndCountryAndCity(EntryDataDto entryDataDto) throws CovidException;
}
