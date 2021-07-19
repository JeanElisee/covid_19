package com.zalance.covid.scheduler;

import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.constant.ErrorCode;
import com.zalance.covid.constant.Status;
import com.zalance.covid.dto.ApiRetryDto;
import com.zalance.covid.exception.RetryException;
import com.zalance.covid.service.FeedService;
import com.zalance.notification.exception.NotificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FeedScheduler {
    Logger logger = LoggerFactory.getLogger(FeedScheduler.class);

    @Value("${zalance.scheduler.enabled}")
    private boolean isSchedulerEnabled;
    @Value("${zalance.queue.covid.name.country}")
    private String covidCountryQueueName;
    @Value("${zalance.queue.covid.name.cases}")
    private String covidCasesQueueName;

    private final RabbitTemplate rabbitTemplate;
    private final FeedService feedService;

    public FeedScheduler(RabbitTemplate rabbitTemplate, FeedService feedService) {
        this.rabbitTemplate = rabbitTemplate;
        this.feedService = feedService;
    }

    //                     ms     sec  min  hour
    @Scheduled(fixedRate = 1000 * 60 * 60 * 72)
    public void countryScheduler() throws NotificationException {
        if (!isSchedulerEnabled) return;
        logger.info("Starting scheduled job to feed countries");
        try {
            feedService.getCountryAndSave(ApiCallType.NORMAL);
        } catch (RetryException retryException) {
            if (retryException.getErrorCode() == ErrorCode.NEED_TO_RETRY.getFieldValue()) {
                ApiRetryDto apiRetryDto = new ApiRetryDto(ApiCallType.COUNTRY, Status.FAILED, LocalDateTime.now(), retryException.getMessage());
                rabbitTemplate.convertAndSend(covidCountryQueueName, apiRetryDto);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    //                     ms     sec  min  hour
    @Scheduled(fixedRate = 1000 * 60 * 60 * 4, initialDelay = 1000 * 15)
    public void covidCasesScheduler() {
        if (!isSchedulerEnabled) return;
        logger.info("Starting scheduled job to feed cases in DB");
        try {
            feedService.getDataFromApi(ApiCallType.NORMAL);
        } catch (RetryException retryException) {
            if (retryException.getErrorCode() == ErrorCode.NEED_TO_RETRY.getFieldValue()) {
                ApiRetryDto apiRetryDto = new ApiRetryDto(ApiCallType.CASES_SUMMARY, Status.FAILED, LocalDateTime.now(), retryException.getMessage());
                rabbitTemplate.convertAndSend(covidCasesQueueName, apiRetryDto);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
