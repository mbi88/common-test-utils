package com.mbi.fakers;

import com.mbi.parameters.Parameter;

/**
 * Replaces parameter with appropriate data.
 */
public interface Fakeable {
    Object fake(String sourceString, Parameter parameter);
}
