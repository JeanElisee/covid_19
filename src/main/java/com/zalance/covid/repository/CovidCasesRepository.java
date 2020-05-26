package com.zalance.covid.repository;

import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.CovidCases;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CovidCasesRepository extends JpaRepository<CovidCases, Long> {
    Page<CovidCases> findAllByCountryOrderByDateDesc(Country c, Pageable p);

    Page<CovidCases> findAllByDateOrderByDateDesc(Date d, Pageable p);

    List<CovidCases> findAllByCountryAndDateOrderByDateDesc(Country c, Date d);

    List<CovidCases> findAllByCountryAndDateAndCityOrderByDateDesc(Country c, Date d, String city);

    List<CovidCases> findByCityAndCountry(String city, Country country);
}
