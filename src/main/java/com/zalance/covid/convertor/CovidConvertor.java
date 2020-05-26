package com.zalance.covid.convertor;

import com.zalance.covid.domain.Country;
import com.zalance.covid.domain.CovidCases;
import com.zalance.covid.domain.GlobalCases;
import com.zalance.covid.dto.CountryDataDto;
import com.zalance.covid.dto.EntryDataDto;
import com.zalance.covid.dto.GlobalCasesVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CovidConvertor {
    CovidConvertor INSTANCE = Mappers.getMapper(CovidConvertor.class);

    @Mapping(source = "country", target = "country")
    CovidCases convertToCovidCases(EntryDataDto entryDataDto, Country country);

    @Mapping(source = "countryDataDto.slug", target = "commonName")
    Country convertToCountry(CountryDataDto countryDataDto);

    @Mapping(source = "isGlobal", target = "isGlobal")
    GlobalCases convertToGlobalCases(GlobalCasesVo globalCasesVo, boolean isGlobal);
}
