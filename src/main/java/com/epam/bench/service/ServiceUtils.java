package com.epam.bench.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.epam.bench.config.Constants.INCOMING_DATE_PATTERN;

public class ServiceUtils {

    private static final DateTimeFormatter formatter_yyyyMMdd = DateTimeFormatter.ofPattern(INCOMING_DATE_PATTERN);

    public static String getFormattedDate(final ZonedDateTime dateToFormat) {
        return dateToFormat.format(formatter_yyyyMMdd);
    }
}
