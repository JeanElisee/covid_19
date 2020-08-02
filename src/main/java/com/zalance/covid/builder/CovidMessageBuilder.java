package com.zalance.covid.builder;

import com.zalance.covid.constant.NotificationCategory;
import com.zalance.covid.dto.CovidNotificationRequestDto;
import com.zalance.notification.builder.MessageBuilder;
import com.zalance.notification.constant.NotificationRequestType;
import com.zalance.notification.exception.NotificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CovidMessageBuilder extends MessageBuilder {
    @Value("${zalance.message.template.path.push.welcome}")
    private String welcomePushTemplatePath;
    @Value("${zalance.message.template.path.sms.welcome}")
    private String welcomeSmsTemplatePath;
    @Value("${zalance.message.template.path.sms.report}")
    private String reportSmsTemplatePath;

    public CovidNotificationRequestDto addDetailsToMessage(CovidNotificationRequestDto notificationRequestDto) throws NotificationException {
        Map<String, Object> params = new HashMap<>();

        if (notificationRequestDto.getNotificationCategory().equals(NotificationCategory.WELCOME.name())) {
            if (notificationRequestDto.getNotificationRequestTypes().contains(NotificationRequestType.PUSH_NOTIFICATION)) {
                params.put("test", "Bienvenue ceci est votre push.");
                notificationRequestDto.setPushMessage(this.buildMessage(welcomePushTemplatePath, params));
            }

            if (notificationRequestDto.getNotificationRequestTypes().contains(NotificationRequestType.SMS)) {
                params.put("test", "SMS envoyé automatiquement");
                notificationRequestDto.setSmsMessage(this.buildMessage(welcomeSmsTemplatePath, params));
            }
        }

        if (notificationRequestDto.getNotificationCategory().equals(NotificationCategory.REPORT.name())) {
            if (notificationRequestDto.getNotificationRequestTypes().contains(NotificationRequestType.SMS)) {
                params.put("saved", notificationRequestDto.getApiCallHistory().getSaved());
                params.put("updated", notificationRequestDto.getApiCallHistory().getUpdated());
                params.put("noChange", notificationRequestDto.getApiCallHistory().getNoChange());
                notificationRequestDto.setSmsMessage(this.buildMessage(reportSmsTemplatePath, params));
            }
        }

        System.out.println(notificationRequestDto.toString());

        return notificationRequestDto;
    }

    @Override
    public String getMailDetails(String notificationCategory) {
        return null;
    }
}
