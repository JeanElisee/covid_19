package com.zalance.covid.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class LocalDateMapper {
    public static LocalDate asLocalDate(LocalDateTime date) {
        return date.toLocalDate();
    }

    public static LocalTime asLocalTime(LocalDateTime date) {
        return date.toLocalTime();
    }
}
