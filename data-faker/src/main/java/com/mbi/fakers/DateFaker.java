package com.mbi.fakers;

import com.mbi.parameters.Parameter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Replaces parameter with current date.
 */
public class DateFaker implements Fakeable {

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern(DATE_PATTERN);

    @Override
    public String fake(final String sourceString, final Parameter parameter) {
        return sourceString.replace(parameter.getFullParameter(), DATE_FORMATTER.print(new DateTime()));
    }
}
