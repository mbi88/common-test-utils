package com.mbi.fakers;

import com.mbi.parameters.Parameter;
import org.apache.commons.lang3.Validate;

import java.util.Objects;
import java.util.Random;

/**
 * Replaces parameter with random number.
 */
public class NumberFaker implements Fakeable {

    /**
     * Returns random number.
     *
     * @param count digits count
     * @return number
     */
    private long getRandomNum(final int count) {
        final String s = String.valueOf(getRandomNum());

        Validate.exclusiveBetween(0, s.length() + 1, count,
                String.format("Value %d is not in the specified exclusive range of %d to %d", count, 1, s.length()));

        // Replace 0 in the beginning
        String result = s.substring(s.length() - count);
        if (Objects.equals(result.charAt(0), '0')) {
            result = result.replaceFirst("^.", "8");
        }

        return Long.parseLong(result);
    }

    /**
     * Returns random number.
     *
     * @return number
     */
    private long getRandomNum() {
        return System.currentTimeMillis() + new Random().nextInt(100_000) + 1;
    }

    @Override
    public Object fake(final String sourceString, final Parameter parameter) {
        final String randomNumber = (parameter.getArguments().isEmpty())
                ? String.valueOf(getRandomNum())
                : String.valueOf(getRandomNum(getDigitsCount(parameter)));

        final String result = sourceString.replace(parameter.getFullParameter(), randomNumber);

        try {
            Long.parseLong(result);
        } catch (NumberFormatException e) {
            return result;
        }

        return Long.parseLong(result);
    }

    private int getDigitsCount(final Parameter parameter) {
        final int count;
        try {
            count = Integer.parseInt(parameter.getArguments().get(0));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Unexpected digits count! Expected int value: %s", parameter.getFullParameter()), e);
        }

        return count;
    }
}
