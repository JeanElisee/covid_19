package com.zalance.covid.listener;

import com.zalance.covid.dto.ApiRetryVo;
import com.zalance.covid.scheduler.FeedScheduler;
import com.zalance.covid.service.FailOverService;
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
public class CasesListener {
    Logger logger = LoggerFactory.getLogger(CasesListener.class);

    @Autowired
    private FeedScheduler feedScheduler;
    @Autowired
    private FailOverService failOverService;

    @Value("${zalance.covid.x-msg-ttl.name}")
    private int xMsgTtl;

    @RabbitListener(queues = "${zalance.covid.cases.queue.name}")
    public void receiveMessage(ApiRetryVo apiRetryVo, @Header(required = false, name = "x-death") List<Map<String, Object>> xDeath) {
        if (apiRetryVo == null) {
            logger.info("Received null in country listener");
            return;
        }

        logger.info("Country retry message received : {}", apiRetryVo);

        try {
            feedScheduler.fetchAndFeedLocalDb();
        } catch (Exception exception) {
            if (failOverService.canRetry(xDeath)) {
                logger.error("An error occurred when fetching the cases, Will retry in {}min...", xMsgTtl);
                throw new AmqpRejectAndDontRequeueException("Failed to process message");
            }
        }
    }
}
