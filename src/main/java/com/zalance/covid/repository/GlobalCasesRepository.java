package com.zalance.covid.repository;

import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.GlobalCases;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface GlobalCasesRepository extends JpaRepository<GlobalCases, Long> {
    Page<GlobalCases> findAllByCountryOrderByCaseDateDesc(Country country, Pageable p);

    Page<GlobalCases> findAllByCaseDateOrderByCaseDateDesc(Date d, Pageable p);

    List<GlobalCases> findAllByCountryAndCaseDateAndNewConfirmedAndNewDeathsAndNewRecoveredAndTotalConfirmedOrderByCaseDateDesc(Country c, Date d, Long newConfirmed, Long newDeaths, Long newRecovered, Long totalConfirmed);
}
