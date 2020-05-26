package com.zalance.covid.repository;

import com.zalance.covid.domain.ApiCallHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiCallHistoryRepository extends JpaRepository<ApiCallHistory, Long> {
}
