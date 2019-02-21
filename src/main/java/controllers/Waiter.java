package controllers;

import org.joda.time.DateTime;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Waiting for special object condition. Condition is checked every 1 sec.
 *
 * @param <T>
 */
public final class Waiter<T> {

	/**
	 * Time in seconds to be spent on waiting of condition.
	 */
	private final int waitingTime;

	/**
	 *
	 */
	private final Supplier<T> function;

	/**
	 *
	 */
	private final Function<T, String> result;

	private boolean printResultWhileWait = false;

	/**
	 * Waiter constructor.
	 *
	 * @param function    d.
	 * @param result      string representation of function result to be used if exception occurs.
	 * @param waitingTime how many seconds waiter will wait until throw exception.
	 */
	public Waiter(final Supplier<T> function, final Function<T, String> result, final int waitingTime) {
		this.function = function;
		this.result = result;
		this.waitingTime = waitingTime;
	}

	/**
	 * Waiter constructor.
	 *
	 * @param function    d.
	 * @param result      string representation of function result to be used if exception occurs.
	 * @param waitingTime how many seconds waiter will wait until throw exception.
	 */
	public Waiter(final Supplier<T> function, final Function<T, String> result, final int waitingTime, final boolean printResultWhileWait) {
		this.function = function;
		this.result = result;
		this.waitingTime = waitingTime;
		this.printResultWhileWait = printResultWhileWait;
	}

	/**
	 * @param expectedCondition condition.
	 * @return result response.
	 */
	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	public T waitCondition(final Predicate<T> expectedCondition) {
		T r = function.get();
		final long startTime = DateTime.now().getMillis();

		while (!expectedCondition.test(r)) {
			if (!waiting(startTime)) {
				throw new Error(String.format(
						"Expected conditions not met. Max waiting time exceeded%n%nResult: %s%n",
						result.apply(r)));
			}

			r = function.get();
		}

		return r;
	}

	/**
	 * Whether waiter should sleep or throw exception.
	 * Sleep 1 second.
	 *
	 * @param startTime time before first request.
	 */
	private boolean waiting(final long startTime) {
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
		scheduledExecutorService.schedule(() -> {
					String res;
					res = printResultWhileWait ? result.apply(function.get()) : "23";
					System.out.println(res);
					return res;
				},
				1,
				TimeUnit.SECONDS);

		return (startTime + waitingTime * 1000L) > DateTime.now().getMillis();
	}
}
