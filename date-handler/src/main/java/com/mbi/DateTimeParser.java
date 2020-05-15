package com.mbi;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts time period formula to custom date time object. Formula example: "1y2M3d4h5m6s"
 * Where:
 * 1y - one year.
 * 2M - two months.
 * 3d - three days.
 * 4h - four hours.
 * 5m - 5 minutes.
 * 6s - 6 seconds.
 */
final class DateTimeParser {

    /**
     * Parses date time string in format:
     * Years - y.
     * Months - M.
     * Days - d.
     * Hours - h.
     * Minutes - m.
     * Seconds - s.
     *
     * @param formula date time.
     * @return parsed object.
     */
    public CustomDateTime parse(final String formula) {
        // Object mapper can't deal with M (mo) and m (m)
        final String dt = formula.replace("M", "mo");

        // Date time should start with digit and end with letter
        if (!dt.substring(0, 1).matches("\\d") || !dt.substring(dt.length() - 1).matches("\\pL")) {
            throw new IllegalArgumentException("Invalid date time format: should start with digit and end with letter");
        }

        final Map<String, Integer> map = new HashMap<>();
        // Split by digits and letters
        final var list = Arrays.asList(dt.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)"));
        for (int i = 0; i < list.size(); i = i + 2) {
            // Odd = key, even = value
            map.put(list.get(i + 1), Integer.valueOf(list.get(i)));
        }

        return new ObjectMapper().convertValue(map, CustomDateTime.class);
    }
}
