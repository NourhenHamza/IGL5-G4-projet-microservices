package tn.esprit.spring.resilience;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ResilienceTestService {

    private int attemptCounter = 0;

    /**
     * TEST 1 : Circuit Breaker
     * Simule un service qui ?choue r?guli?rement
     */
    @CircuitBreaker(name = "testCircuitBreaker", fallbackMethod = "fallbackCircuitBreaker")
    public String testCircuitBreaker(boolean shouldFail) {
        log.info("?? Circuit Breaker - Tentative d'appel (shouldFail={})", shouldFail);
        
        if (shouldFail) {
            log.error("? Service ?choue - Exception lev?e");
            throw new RuntimeException("Service indisponible");
        }
        
        log.info("? Service r?pond correctement");
        return "Circuit Breaker : Service op?rationnel";
    }

    private String fallbackCircuitBreaker(boolean shouldFail, Exception e) {
        log.warn("?? FALLBACK Circuit Breaker activ? - Raison: {}", e.getMessage());
        return "FALLBACK : Le service est temporairement indisponible (Circuit ouvert)";
    }

    /**
     * TEST 2 : Retry
     * R?essaye automatiquement en cas d'?chec
     */
    @Retry(name = "testRetry", fallbackMethod = "fallbackRetry")
    public String testRetry() {
        attemptCounter++;
        log.info("?? Retry - Tentative #{}", attemptCounter);
        
        if (attemptCounter < 3) {
            log.error("? ?chec de la tentative #{}", attemptCounter);
            throw new RuntimeException("Erreur temporaire");
        }
        
        log.info("? Succ?s ? la tentative #{}", attemptCounter);
        attemptCounter = 0; // Reset
        return "Retry : Succ?s apr?s " + attemptCounter + " tentatives";
    }

    private String fallbackRetry(Exception e) {
        attemptCounter = 0;
        log.warn("?? FALLBACK Retry activ? apr?s plusieurs ?checs");
        return "FALLBACK : ?chec apr?s 3 tentatives - " + e.getMessage();
    }

    /**
     * TEST 3 : Rate Limiter
     * Limite le nombre d'appels par seconde
     */
    @RateLimiter(name = "testRateLimiter", fallbackMethod = "fallbackRateLimiter")
    public String testRateLimiter() {
        log.info("?? Rate Limiter - Appel autoris?");
        return "Rate Limiter : Requ?te trait?e (limite: 2/seconde)";
    }

    private String fallbackRateLimiter(Exception e) {
        log.warn("?? FALLBACK Rate Limiter - Trop de requ?tes");
        return "FALLBACK : Limite de requ?tes d?pass?e - R?essayez dans quelques secondes";
    }

    /**
     * TEST 4 : Time Limiter (Timeout)
     * Simule un appel qui prend trop de temps
     */
    @TimeLimiter(name = "testTimeLimiter", fallbackMethod = "fallbackTimeLimiter")
    public CompletableFuture<String> testTimeLimiter(int delaySeconds) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("?? Time Limiter - Simulation d?lai de {} secondes", delaySeconds);
            try {
                Thread.sleep(delaySeconds * 1000L);
                log.info("? R?ponse apr?s {} secondes", delaySeconds);
                return "Time Limiter : R?ponse apr?s " + delaySeconds + " secondes";
            } catch (InterruptedException e) {
                log.error("? Thread interrompu");
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrompu", e);
            }
        });
    }

    private CompletableFuture<String> fallbackTimeLimiter(int delaySeconds, Exception e) {
        log.warn("?? FALLBACK Time Limiter - Timeout d?pass? ({}s > 5s)", delaySeconds);
        return CompletableFuture.completedFuture(
            "FALLBACK : Timeout d?pass? - Le service a mis trop de temps ? r?pondre"
        );
    }

    /**
     * TEST 5 : Combinaison (Retry + Circuit Breaker)
     */
    @Retry(name = "testCombined")
    @CircuitBreaker(name = "testCombined", fallbackMethod = "fallbackCombined")
    public String testCombined(boolean shouldFail) {
        log.info("?? Combined (Retry+CircuitBreaker) - Appel");
        
        if (shouldFail) {
            throw new RuntimeException("Service en ?chec");
        }
        
        return "Combined : Service op?rationnel avec Retry + Circuit Breaker";
    }

    private String fallbackCombined(boolean shouldFail, Exception e) {
        log.warn("?? FALLBACK Combined activ?");
        return "FALLBACK : ?chec apr?s retry + circuit breaker";
    }
}
