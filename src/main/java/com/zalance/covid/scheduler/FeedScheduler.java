package com.zalance.covid.scheduler;

import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.constant.Status;
import com.zalance.covid.dto.ApiRetryDto;
import com.zalance.covid.exception.RetryException;
import com.zalance.covid.service.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FeedScheduler {
    Logger logger = LoggerFactory.getLogger(FeedScheduler.class);

    @Value("${zalance.covid.country.queue.name}")
    private String covidCountryQueueName;
    @Value("${zalance.covid.cases.queue.name}")
    private String covidCasesQueueName;

    private final RabbitTemplate rabbitTemplate;
    private final FeedService feedService;

    public FeedScheduler(RabbitTemplate rabbitTemplate, FeedService feedService) {
        this.rabbitTemplate = rabbitTemplate;
        this.feedService = feedService;
    }

    //                     ms    sec  min  hour  day
    @Scheduled(fixedRate = 1000 * 60 * 60 * 24 * 3)
    public void countryScheduler() {
        logger.info("Starting scheduled job to feed countries");
        try {
            feedService.getCountryAndSave(ApiCallType.NORMAL);
        } catch (RetryException retryException) {
            ApiRetryDto apiRetryDto = new ApiRetryDto(ApiCallType.COUNTRY, Status.FAILED, new Date(), retryException.getMessage());
            rabbitTemplate.convertAndSend(covidCountryQueueName, apiRetryDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    //                     ms    sec  min  hour
    @Scheduled(fixedRate = 1000 * 60 * 60 * 10, initialDelay = 1000 * 10)
    public void covidCasesScheduler() {
        logger.info("Starting scheduled job to feed cases in DB");
        try {
            feedService.getDataFromApi(ApiCallType.NORMAL);
        } catch (RetryException retryException) {
            ApiRetryDto apiRetryDto = new ApiRetryDto(ApiCallType.CASES_SUMMARY, Status.FAILED, new Date(), retryException.getMessage());
            rabbitTemplate.convertAndSend(covidCasesQueueName, apiRetryDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
