package com.zalance.covid.repository;

import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.GlobalCases;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GlobalCasesRepository extends JpaRepository<GlobalCases, Long> {
    Page<GlobalCases> findAllByCountryOrderByCaseDateDesc(Country country, Pageable p);

    Page<GlobalCases> findAllByCaseDateOrderByCaseDateDesc(LocalDate caseDate, Pageable pageable);

    List<GlobalCases> findAllByCountryAndCaseDateAndNewConfirmedAndNewDeathsAndNewRecoveredAndTotalConfirmedAndTotalDeathsAndTotalRecovered(Country c, LocalDate caseDate, Long newConfirmed, Long newDeaths, Long newRecovered, Long totalConfirmed, Long totalDeaths, Long totalRecovered);
}
