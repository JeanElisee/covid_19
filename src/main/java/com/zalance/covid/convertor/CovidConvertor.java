package com.zalance.covid.convertor;

import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.GlobalCases;
import com.zalance.covid.dto.CountryDataDto;
import com.zalance.covid.dto.GlobalCasesDto;
import com.zalance.covid.mapper.LocalDateMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, uses = LocalDateMapper.class)
public interface CovidConvertor {
    CovidConvertor INSTANCE = Mappers.getMapper(CovidConvertor.class);

    @Mapping(expression = "java(LocalDateMapper.asLocalDate(globalCasesDto.getDate()))", target = "caseDate")
    @Mapping(expression = "java(LocalDateMapper.asLocalTime(globalCasesDto.getDate()))", target = "caseTime")
    @Mapping(source = "country", target = "country")
    GlobalCases convertToGlobalCases(GlobalCasesDto globalCasesDto, Country country);

    @Mapping(source = "countryDataDto.slug", target = "commonName")
    @Mapping(source = "countryDataDto.country", target = "name")
    Country convertToCountry(CountryDataDto countryDataDto);
}
