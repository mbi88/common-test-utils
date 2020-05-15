package com.mbi.utils;

import com.damnhandy.uri.template.UriTemplate;

/**
 * Common utils.
 */
public final class Commons {

    private Commons() {
    }

    public static String buildPathParams(final String url, final Object... pathParams) {
        if (pathParams == null || UriTemplate.fromTemplate(url).getVariables().length == 0) {
            return url;
        }

        final var template = UriTemplate.fromTemplate(url);

        var result = "";
        for (int i = 0; i < template.getVariables().length; i++) {
            result = template.set(template.getVariables()[i], pathParams[i]).expand();
        }

        return result;
    }
}
