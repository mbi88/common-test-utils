package com.mbi.fakers;

import com.mbi.parameters.Parameter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Replaces parameter with caller method name.
 */
public class CallerFaker implements Fakeable {

    /**
     * Helps to find test case element in stacktrace.
     */
    private final Predicate<StackTraceElement> testCaseInStackTrace = element -> {
        final boolean invokedFromMethod = element.getClassName().contains("reflect.")
                && element.getClassName().contains("MethodAccessor")
                && element.getMethodName().startsWith("invoke");
        final boolean invokedFromClassField = element.getClassName().contains("NativeConstructorAccessorImpl")
                && element.getMethodName().equals("newInstance0");

        return invokedFromMethod || invokedFromClassField;
    };

    @Override
    public String fake(final String sourceString, final Parameter parameter) {
        return sourceString.replace(parameter.getFullParameter(), getCallerMethodName());
    }

    /**
     * Catch caller method from stacktrace.
     *
     * @return stack trace depth.
     */
    private int getDepth() {
        int depth = 0;
        final List<StackTraceElement> elements = new ArrayList<>(Arrays.asList(Thread.currentThread().getStackTrace()));

        for (StackTraceElement element : elements) {
            if (testCaseInStackTrace.test(element)) {
                depth = elements.indexOf(element);
                break;
            }
        }

        return depth - 2;
    }

    /**
     * Get caller class and method name.
     *
     * @return caller class and method.
     */
    private String getCallerMethodName() {
        final StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[getDepth()];

        return String.format("%s.%s", stackTraceElement.getClassName(), stackTraceElement.getMethodName());
    }
}
