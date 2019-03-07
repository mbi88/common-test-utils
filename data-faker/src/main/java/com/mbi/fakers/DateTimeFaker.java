package com.mbi.fakers;

import com.mbi.parameters.Parameter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Replaces parameter with current datetime.
 */
public class DateTimeFaker implements Fakeable {

    private static final String DATETIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern(DATETIME_PATTERN);

    @Override
    public String fake(final String sourceString, final Parameter parameter) {
        return sourceString.replace(parameter.getFullParameter(), DATE_TIME_FORMATTER.print(new DateTime()));
    }
}
