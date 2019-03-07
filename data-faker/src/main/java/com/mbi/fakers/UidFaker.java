package com.mbi.fakers;

import com.mbi.parameters.Parameter;

/**
 * Replaces parameter with uid.
 */
public class UidFaker implements Fakeable {
    @Override
    public String fake(final String sourceString, final Parameter parameter) {
        return sourceString.replace(parameter.getFullParameter(), java.util.UUID.randomUUID().toString());
    }
}
