package com.zalance.covid.listener;

import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.constant.ErrorCode;
import com.zalance.covid.dto.ApiRetryDto;
import com.zalance.covid.exception.RetryException;
import com.zalance.covid.service.CovidFailOverService;
import com.zalance.covid.service.FeedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class CountryListener {
    Logger logger = LoggerFactory.getLogger(CountryListener.class);

    @Value("${zalance.notification.retry.x-msg-ttl}")
    private int xMsgTtl;

    private final FeedService feedService;
    private final CovidFailOverService covidFailOverService;

    public CountryListener(FeedService feedService, CovidFailOverService covidFailOverService) {
        this.feedService = feedService;
        this.covidFailOverService = covidFailOverService;
    }

    @RabbitListener(queues = "${zalance.queue.covid.name.country}")
    public void receiveMessage(ApiRetryDto apiRetryDto, @Header(required = false, name = "x-death") List<Map<String, Object>> xDeath) throws RetryException {
        if (apiRetryDto == null) {
            logger.info("Received null in country listener");
            return;
        }

        logger.info("Country retry message received : {}", apiRetryDto);

        try {
            feedService.getCountryAndSave(ApiCallType.RETRY);
        } catch (RetryException retryException) {
            if (covidFailOverService.canRetry(xDeath) && retryException.getErrorCode() == ErrorCode.ANOTHER_ATTEMPT.getFieldValue()) {
                logger.error("An error occurred when retrying to fetch countries from API, Will retry in {} min...", xMsgTtl);
                throw new AmqpRejectAndDontRequeueException("Failed to fetch countries from API");
            }
        } catch (Exception exception) {
            logger.error("A error occurred when retrying to fetch countries from API, no retry will be done. {}", exception.toString());
        }
    }
}
