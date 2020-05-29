package com.zalance.covid.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CovidFailOverService {
    Logger logger = LoggerFactory.getLogger(CovidFailOverService.class);

    @Value("${zalance.notification.max-retry.value}")
    private int maxRetry;

    public boolean canRetry(List<Map<String, Object>> xDeath) {
        long retryCount = 0L;
        if (xDeath != null) {
            Optional<Long> count = xDeath.stream()
                    .flatMap(m -> m.entrySet().stream())
                    .filter(e -> e.getKey().equals("count"))
                    .findFirst().map(e -> (Long) e.getValue());
            retryCount = count.map(Long::longValue).orElse(retryCount);
        }

        System.out.println(retryCount);

        if (retryCount <= maxRetry) {
            System.out.println("Retrying...");

            return true;
        } else {
            logger.info("exceed max retry the message will be dropped");
            return false;
        }
    }
}
