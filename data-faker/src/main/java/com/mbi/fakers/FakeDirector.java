package com.mbi.fakers;

import com.mbi.parameters.Parameter;
import com.mbi.parameters.SupportedParameters;

/**
 * Conducts value updating according to passed update parameter.
 * See {@link SupportedParameters} for available supported parameters.
 */
public class FakeDirector {

    /**
     * Directs value updating according to update parameter.
     *
     * @param parameter  parameter to be replaced.
     * @param fieldValue source field value.
     * @return updated field value.
     * @throws IllegalArgumentException if unsupported parameter passed.
     */
    public Object fake(final String parameter, final Object fieldValue) {
        final Object updatedStr;
        final Parameter supportedParameter = new Parameter(parameter);

        switch (supportedParameter.getSupportedParameter()) {
            case CALLER:
                updatedStr = new CallerFaker().fake(String.valueOf(fieldValue), supportedParameter);
                break;
            case UID:
                updatedStr = new UidFaker().fake(String.valueOf(fieldValue), supportedParameter);
                break;
            case DATE:
                updatedStr = new DateFaker().fake(String.valueOf(fieldValue), supportedParameter);
                break;
            case DATETIME:
                updatedStr = new DateTimeFaker().fake(String.valueOf(fieldValue), supportedParameter);
                break;
            case CURRENT_DATE:
                updatedStr = new DateFaker().fake(String.valueOf(fieldValue), supportedParameter);
                break;
            case CURRENT_DATETIME:
                updatedStr = new DateTimeFaker().fake(String.valueOf(fieldValue), supportedParameter);
                break;
            case NUMBER:
                updatedStr = new NumberFaker().fake(String.valueOf(fieldValue), supportedParameter);
                break;
            default:
                throw new IllegalArgumentException("Unsupported parameter: " + parameter);
        }

        return updatedStr;
    }
}
