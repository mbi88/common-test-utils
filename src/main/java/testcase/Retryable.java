package testcase;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Custom annotation that signals that a configuration method should be retried.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, TYPE})
public @interface Retryable {

    /**
     * @return - How many times should a configuration be retried.
     */
    int attempts() default 3;
}
