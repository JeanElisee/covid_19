package com.zalance.covid.convertor;

import com.zalance.covid.constant.NotificationCategory;
import com.zalance.covid.domain.ApiCallHistory;
import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.GlobalCases;
import com.zalance.covid.dto.CountryDataDto;
import com.zalance.covid.dto.CovidNotificationRequestDto;
import com.zalance.covid.dto.GlobalCasesDto;
import com.zalance.covid.mapper.LocalDateMapper;
import com.zalance.notification.constant.NotificationRequestType;
import com.zalance.notification.dto.NotificationRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = LocalDateMapper.class, imports = {LocalDateMapper.class, NotificationRequestType.class, java.util.Collections.class}, nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CovidConvertor {
    CovidConvertor INSTANCE = Mappers.getMapper(CovidConvertor.class);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(expression = "java(LocalDateMapper.asLocalDate(globalCasesDto.getDate()))", target = "caseDate"),
            @Mapping(expression = "java(LocalDateMapper.asLocalTime(globalCasesDto.getDate()))", target = "caseTime"),
            @Mapping(source = "country", target = "country")
    })
    GlobalCases convertToGlobalCases(GlobalCasesDto globalCasesDto, Country country);

    @Mappings({
            @Mapping(source = "globalCases.id", target = "id"),
            @Mapping(source = "globalCasesDto.newConfirmed", target = "newConfirmed"),
            @Mapping(source = "globalCasesDto.totalConfirmed", target = "totalConfirmed"),
            @Mapping(source = "globalCasesDto.newDeaths", target = "newDeaths"),
            @Mapping(source = "globalCasesDto.totalDeaths", target = "totalDeaths"),
            @Mapping(source = "globalCasesDto.newRecovered", target = "newRecovered"),
            @Mapping(source = "globalCasesDto.totalRecovered", target = "totalRecovered"),
            @Mapping(source = "globalCases.global", target = "global"),
            @Mapping(source = "globalCases.caseDate", target = "caseDate"),
            @Mapping(expression = "java(LocalDateMapper.asLocalTime(globalCasesDto.getDate()))", target = "caseTime"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(source = "country", target = "country")
    })
    GlobalCases convertToGlobalCases(GlobalCasesDto globalCasesDto, GlobalCases globalCases, Country country);

    @Mappings({
            @Mapping(source = "countryDataDto.slug", target = "commonName"),
            @Mapping(source = "countryDataDto.country", target = "name"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true)
    })
    Country convertToCountry(CountryDataDto countryDataDto);

    // For SMS
    @Mappings({
            @Mapping(source = "notificationCategory", target = "notificationCategory"),
            @Mapping(source = "phoneNumber", target = "to"),
            @Mapping(expression = "java(Collections.singletonList(NotificationRequestType.SMS))", target = "notificationRequestTypes"),
            @Mapping(source = "apiCallHistory", target = "apiCallHistory"),
            @Mapping(target = "name", ignore = true),
            @Mapping(target = "email", ignore = true),
            @Mapping(target = "templateId", ignore = true),
            @Mapping(target = "smsMessage", ignore = true),
            @Mapping(target = "pushTitle", ignore = true),
            @Mapping(target = "pushMessage", ignore = true),
            @Mapping(target = "pushTopic", ignore = true),
            @Mapping(target = "pushToken", ignore = true)
    })
    CovidNotificationRequestDto convertToNotificationRequest(ApiCallHistory apiCallHistory, String phoneNumber, NotificationCategory notificationCategory);


    NotificationRequestDto convertToNotificationRequest(CovidNotificationRequestDto covidNotificationRequestDto);
}
