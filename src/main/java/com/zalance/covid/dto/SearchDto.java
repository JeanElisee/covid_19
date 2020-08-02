package com.zalance.covid.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@ToString
public class SearchDto {
    private String countryCode;
    private LocalDate date;
}
