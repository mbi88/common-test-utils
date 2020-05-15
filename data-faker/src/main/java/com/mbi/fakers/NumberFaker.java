package com.mbi.fakers;

import com.mbi.parameters.Parameter;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.List;
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
        // Valid range
        final int start = 1;
        final int end = 18;
        // Validate digits count is in a supported range
        Validate.exclusiveBetween(start - 1, end + 1, count,
                String.format("Value %d is not in the specified exclusive range of %d to %d", count, start, end));

        final List<Integer> integers = new ArrayList<>();
        final var randomGenerator = new Random();

        for (int i = 0; i < count; i++) {
            integers.add(randomGenerator.nextInt(10));
        }

        // Replace 0 in the beginning
        if (integers.get(0).equals(0)) {
            integers.set(0, 1);
        }

        final var randomString = new StringBuilder();
        for (var i : integers) {
            randomString.append(i);
        }

        return Long.parseLong(String.valueOf(randomString));
    }

    /**
     * Returns random number.
     *
     * @return number
     */
    private long getRandomNum() {
        return getRandomNum(13);
    }

    @Override
    public Object fake(final String sourceString, final Parameter parameter) {
        final var randomNumber = (parameter.getArguments().isEmpty())
                ? String.valueOf(getRandomNum())
                : String.valueOf(getRandomNum(getDigitsCount(parameter)));

        final var result = sourceString.replace(parameter.getFullParameter(), randomNumber);

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
