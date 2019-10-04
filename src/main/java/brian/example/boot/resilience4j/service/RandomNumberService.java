package brian.example.boot.resilience4j.service;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.vavr.control.Try;

@Service
public class RandomNumberService {

	private static final CircuitBreaker circuitBreaker;
    private static final TimeLimiter timeLimiter;
	
    // TimeLimit CircuitBreak setup
    static {
        long ttl = 5000;
        TimeLimiterConfig configTimerLimit
                = TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(ttl)).build();
        timeLimiter = TimeLimiter.of(configTimerLimit);

        circuitBreaker = CircuitBreaker.of("my", CircuitBreakerConfig.ofDefaults());
    }
	
    /**
     * Controller will call this method
     * 
     * @param timeDelay
     * @return
     */
	public int getDelayedRandomNumber(int timeDelay) {
		
        Callable<Integer> integerCallable = TimeLimiter.decorateFutureSupplier( timeLimiter,
                								() -> CompletableFuture.supplyAsync(
                												() -> delayRandomNumber(timeDelay)
                											)
        									);

        Callable<Integer> callable = CircuitBreaker.decorateCallable(circuitBreaker, integerCallable);

        return Try.of(callable::call).recover(t -> fallback(timeDelay, t)).get();
	}
	
	/**
	 * This method can cause time delay
	 * 
	 * @param timeDelay
	 * @return
	 */
	private int delayRandomNumber(int timeDelay) {
		try {
			System.out.println("Delaying starts...");
			Thread.sleep(timeDelay); // The execution will be delayed this long
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Delaying ended...");
		}
		
		return timeDelay;
	}
	
	/**
	 * If time delay happened, this will be called.
	 * It will return -1 if the timeDelay was odd number, otherwise it will throw RuntimeException
	 * 
	 * @param timeDelay
	 * @param t
	 * @return
	 */
	private int fallback(int timeDelay, Throwable t) {
		
		t.printStackTrace();
		
		if( timeDelay % 2 == 0 )
			throw new RuntimeException("Something happened");
		else
			return -1;
	}
	
}
