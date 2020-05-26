package com.zalance.covid.scheduler;

import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.service.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FeedScheduler {
    Logger logger = LoggerFactory.getLogger(FeedScheduler.class);

    private final FeedService feedService;

    public FeedScheduler(FeedService feedService) {
        this.feedService = feedService;
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24 * 3)
    public void fetchCountryAndFeedLocalDb() {
        logger.info("Starting scheduled job to feed countries");
        try {
            feedService.getCountryAndSave();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

//    @Scheduled(fixedRate = 1000 * 60 * 60 * 12, initialDelay = 5000)
//    public void fetchAndFeedLocalDb() {
//        logger.info("Starting scheduled job to feed the DB from second schema");
//        try {
//            feedService.getDataFromApi("ALL");
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 8, initialDelay = 5000)
    public void fetchAndFeedLocalDb() {
        logger.info("Starting scheduled job to feed the DB from second schema");
        try {
            feedService.getDataFromApi(ApiCallType.CASES_SUMMARY);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
