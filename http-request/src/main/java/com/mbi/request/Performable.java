package com.mbi.request;

/**
 * Called upon request.
 */
@FunctionalInterface
public interface Performable {
    void onRequest();
}
