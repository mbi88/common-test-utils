package com.mbi;

import com.mbi.fakers.FakeDirector;
import com.mbi.parameters.SupportedParameters;

/**
 * Set update parameter in object to get it replaced with corresponding data.
 * Example:
 * {
 * "field": "Today is {$current_date}."
 * }
 * <p>
 * Returns:
 * {
 * "field": "Today is 2019-01-29."
 * }
 * <p>
 * See {@link SupportedParameters} for available supported parameters.
 */
public interface Faker {

    /**
     * Beginning of the update parameter.
     */
    String PARAMETER_START = "{$";

    /**
     * Ending of the update parameter.
     */
    String PARAMETER_END = "}";

    /**
     * Conducts value updating according to passed update parameter.
     */
    FakeDirector FAKE_DIRECTOR = new FakeDirector();

    /**
     * Replace update parameters with appropriate data.
     *
     * @param resource object to be updated.
     * @param <T>      object type.
     * @return updated object.
     */
    <T> T fakeData(T resource);
}
