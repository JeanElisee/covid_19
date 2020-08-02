package com.zalance.covid.publisher;

import com.zalance.covid.convertor.CovidConvertor;
import com.zalance.covid.dto.CovidNotificationRequestDto;
import com.zalance.notification.constant.NotificationRequestType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationPublisher {
    Logger logger = LoggerFactory.getLogger(NotificationPublisher.class);

    @Value("${zalance.queue.name.email}")
    private String emailQueueName;
    @Value("${zalance.queue.name.push-notification}")
    private String pushNotificationQueueName;
    @Value("${zalance.queue.name.sms}")
    private String smsQueueName;

    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(CovidNotificationRequestDto covidNotificationRequestDto) {
        if (covidNotificationRequestDto.getNotificationRequestTypes().contains(NotificationRequestType.PUSH_NOTIFICATION)) {
            convertAndPublish(pushNotificationQueueName, covidNotificationRequestDto);
        }
        if (covidNotificationRequestDto.getNotificationRequestTypes().contains(NotificationRequestType.SMS)) {
            convertAndPublish(smsQueueName, covidNotificationRequestDto);
        }
        if (covidNotificationRequestDto.getNotificationRequestTypes().contains(NotificationRequestType.EMAIL)) {
            convertAndPublish(emailQueueName, covidNotificationRequestDto);
        }

        if (!covidNotificationRequestDto.getNotificationRequestTypes().contains(NotificationRequestType.EMAIL)
                && !covidNotificationRequestDto.getNotificationRequestTypes().contains(NotificationRequestType.SMS)
                && !covidNotificationRequestDto.getNotificationRequestTypes().contains(NotificationRequestType.PUSH_NOTIFICATION)) {
            logger.info("Type of message and queue not define, consider adding the details in the publisher");
        }
    }

    private void convertAndPublish(String queueName, CovidNotificationRequestDto covidNotificationRequestDto) {
        rabbitTemplate.convertAndSend(queueName, CovidConvertor.INSTANCE.convertToNotificationRequest(covidNotificationRequestDto));
    }
}
