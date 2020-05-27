package com.zalance.covid.listener;

import com.zalance.covid.constant.ApiCallType;
import com.zalance.covid.dto.ApiRetryDto;
import com.zalance.covid.exception.RetryException;
import com.zalance.covid.service.FailOverService;
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

    @Autowired
    private FeedService feedService;
    @Autowired
    private FailOverService failOverService;

    @Value("${zalance.covid.x-msg-ttl.name}")
    private int xMsgTtl;

    @RabbitListener(queues = "${zalance.covid.country.queue.name}")
    public void receiveMessage(ApiRetryDto apiRetryDto, @Header(required = false, name = "x-death") List<Map<String, Object>> xDeath) {
        if (apiRetryDto == null) {
            logger.info("Received null in country listener");
            return;
        }

        logger.info("Country retry message received : {}", apiRetryDto);

        try {
            feedService.getCountryAndSave(ApiCallType.RETRY);
        } catch (RetryException retryException) {
            if (failOverService.canRetry(xDeath)) {
                logger.error("An error occurred when retrying the countries, Will retry in {}min...", xMsgTtl);
                throw new AmqpRejectAndDontRequeueException("Failed to save countries to the DB");
            }
        } catch (Exception exception) {
            logger.error("A error occurred when retrying the countries, no retry will be done. {}", exception.toString());
        }
    }
}
