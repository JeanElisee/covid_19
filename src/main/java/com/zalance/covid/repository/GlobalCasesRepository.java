package com.zalance.covid.repository;

import com.zalance.covid.domain.GlobalCases;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GlobalCasesRepository extends JpaRepository<GlobalCases, Long> {
}
