package com.zalance.covid.repository;

import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.GlobalCases;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GlobalCasesRepository extends JpaRepository<GlobalCases, Long> {
    Page<GlobalCases> findAllByCountryOrderByCaseDateDesc(Country country, Pageable p);

    Page<GlobalCases> findAllByCaseDateOrderByCaseDateDesc(Date d, Pageable p);

    List<GlobalCases> findAllByCountryAndNewConfirmedAndNewDeathsAndNewRecoveredAndTotalConfirmedAndTotalDeathsAndTotalRecovered(Country c, Long newConfirmed, Long newDeaths, Long newRecovered, Long totalConfirmed, Long totalDeaths, Long totalRecovered);
}
