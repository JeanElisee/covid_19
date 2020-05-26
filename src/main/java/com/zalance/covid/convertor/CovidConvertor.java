package com.zalance.covid.convertor;

import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.GlobalCases;
import com.zalance.covid.dto.CountryDataDto;
import com.zalance.covid.dto.GlobalCasesVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CovidConvertor {
    CovidConvertor INSTANCE = Mappers.getMapper(CovidConvertor.class);

    @Mapping(source = "country", target = "country")
    GlobalCases convertToGlobalCases(GlobalCasesVo globalCasesVo, Country country);

    @Mapping(source = "countryDataDto.slug", target = "commonName")
    @Mapping(source = "countryDataDto.country", target = "name")
    Country convertToCountry(CountryDataDto countryDataDto);

    @Mapping(source = "isGlobal", target = "global")
    GlobalCases convertToGlobalCases(GlobalCasesVo globalCasesVo, boolean isGlobal);
}
