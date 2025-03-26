package testcase;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Mark classes or tests with this annotation if you want to be skipped from retrying.
 * Test retry gradle plugin can't exclude a specific test method from retry, so annotation should be added to the class.
 * <a href="https://github.com/gradle/test-retry-gradle-plugin/issues/86">See test-retry-gradle-plugin issue</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface NonRetryable {
}
