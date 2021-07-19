package com.zalance.covid.controller;

import com.zalance.covid.dto.GlobalCasesDto;
import com.zalance.covid.dto.SearchDto;
import com.zalance.covid.exception.CovidException;
import com.zalance.covid.exception.NotFoundException;
import com.zalance.covid.service.CovidCasesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "covid")
public class CovidController {
    Logger logger = LoggerFactory.getLogger(CovidController.class);

    private final CovidCasesService covidCasesService;

    public CovidController(CovidCasesService covidCasesService) {
        this.covidCasesService = covidCasesService;
    }

    @GetMapping("/get-cases")
    ResponseEntity<?> getCases(@PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok().body(covidCasesService.getCases(pageable));
    }

    @PostMapping("/cases-by-country")
    ResponseEntity<?> getCasesByCountry(@RequestBody SearchDto searchDto, Pageable pageable) throws CovidException, NotFoundException {
        logger.info("Searching cases for {}", searchDto.toString());
        return ResponseEntity.ok().body(covidCasesService.getCasesByCountry(searchDto, pageable));
    }

    @PostMapping("/cases-by-date")
    ResponseEntity<?> getCasesByDate(@RequestBody SearchDto searchDto, Pageable pageable) {
        return ResponseEntity.ok().body(covidCasesService.getCasesByDate(searchDto, pageable));
    }

    @PostMapping("/cases-by-country-and-date")
    ResponseEntity<?> getCasesByCountryAndDate(@RequestBody GlobalCasesDto globalCasesDto) throws CovidException, NotFoundException {
        return ResponseEntity.ok().body(covidCasesService.getCasesByDateAndCountry(globalCasesDto));
    }
}
