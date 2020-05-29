package com.zalance.covid.builder;

import com.zalance.notification.builder.MessageBuilder;
import com.zalance.notification.constant.NotificationCategory;
import com.zalance.notification.constant.NotificationRequestType;
import com.zalance.notification.dto.NotificationRequestDto;
import com.zalance.notification.exception.NotificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CovidMessageBuilder extends MessageBuilder {
    @Value("${zalance.message.template.path.push.welcome}")
    private String welcomeMessagePushTemplatePath;
    @Value("${zalance.message.template.path.sms.welcome}")
    private String welcomeMessageSmsTemplatePath;

    public NotificationRequestDto addDetailsToMessage(NotificationRequestDto notificationRequestDto) throws NotificationException {
        Map<String, Object> params = new HashMap<>();

        if (notificationRequestDto.getNotificationRequestTypes() != null)
            for (NotificationRequestType notificationRequestType : notificationRequestDto.getNotificationRequestTypes()) {
                if (notificationRequestDto.getNotificationCategory() == NotificationCategory.WELCOME) {
                    if (notificationRequestType == NotificationRequestType.PUSH_NOTIFICATION) {
                        params.put("test", "Bienvenue ceci est votre push.");
                        notificationRequestDto.setPushMessage(this.buildMessage(welcomeMessagePushTemplatePath, params));
                    }

                    if (notificationRequestType == NotificationRequestType.SMS) {
                        params.put("test", "SMS envoyé automatiquement");
                        notificationRequestDto.setSmsMessage(this.buildMessage(welcomeMessageSmsTemplatePath, params));
                    }
                }
            }

        return notificationRequestDto;
    }

    @Override
    public String getMailDetails(NotificationCategory notificationCategory) {
        return null;
    }
}
