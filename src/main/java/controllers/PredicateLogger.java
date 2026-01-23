package controllers;

import org.slf4j.Logger;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A predicate wrapper that logs the result of each {@link #test(Object)} evaluation.
 * Useful for debugging retry/waiter logic and understanding why conditions do or do not match.
 *
 * @param <T> the input type the predicate operates on
 */
public class PredicateLogger<T> implements Predicate<T> {

    private final Predicate<T> delegate;
    private final Logger logger;
    private final String name;
    private final Function<T, String> stringify;

    /**
     * Constructs a logging predicate wrapper.
     *
     * @param delegate  the original predicate to evaluate
     * @param logger    the SLF4J logger used for output
     * @param name      a name/label for the predicate, used in logs
     * @param stringify function to convert input values into human-readable strings
     */
    public PredicateLogger(
            final Predicate<T> delegate,
            final Logger logger,
            final String name,
            final Function<T, String> stringify
    ) {
        this.delegate = delegate;
        this.logger = logger;
        this.name = name;
        this.stringify = stringify;
    }

    /**
     * Factory method to wrap a predicate with logging.
     *
     * @param predicate the predicate to wrap
     * @param logger    logger to output predicate evaluations
     * @param name      name to display in logs
     * @param stringify function to stringify the input value
     * @return a predicate that logs its result
     */
    public static <T> Predicate<T> log(
            final Predicate<T> predicate,
            final Logger logger,
            final String name,
            final Function<T, String> stringify
    ) {
        return new PredicateLogger<>(predicate, logger, name, stringify);
    }

    /**
     * Evaluates the predicate and logs the result, including the input.
     * Logs a warning if the predicate or stringifier throws an exception.
     *
     * @param t the input argument
     * @return the result of the delegate predicate
     */
    @Override
    public boolean test(final T t) {
        final boolean result;
        try {
            result = delegate.test(t);
        } catch (Exception e) {
            logger.warn("Predicate [{}] threw exception for input: {}, error: {}",
                    name, stringify.apply(t), e.toString());
            throw e;
        }

        logger.info("Predicate [{}] -> {} | input: {}", name, result, stringify.apply(t));
        return result;
    }
}

