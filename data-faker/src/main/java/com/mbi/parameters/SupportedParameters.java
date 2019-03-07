package com.mbi.parameters;

/**
 * Supported parameters to be replaces with appropriate data.
 */
public enum SupportedParameters {

    UID,
    CALLER,
    INVALID,
    DATE,
    DATETIME,
    NUMBER,

    /**
     * @deprecated Use {@link SupportedParameters#DATE} instead.
     */
    @Deprecated
    CURRENT_DATE,

    /**
     * @deprecated Use {@link SupportedParameters#DATETIME} instead.
     */
    @Deprecated
    CURRENT_DATETIME
}
