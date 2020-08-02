package com.zalance.covid.dto;

import com.zalance.covid.domain.ApiCallHistory;
import com.zalance.notification.constant.NotificationRequestType;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CovidNotificationRequestDto {
    // For email
    private String name;
    private String email;
    private String templateId;

    // For sms
    private String to;
    private String smsMessage;

    // For push
    private String pushTitle;
    private String pushMessage;
    private String pushTopic;
    private String pushToken;

    private String notificationCategory;
    private List<NotificationRequestType> notificationRequestTypes;

    private ApiCallHistory apiCallHistory;
}
