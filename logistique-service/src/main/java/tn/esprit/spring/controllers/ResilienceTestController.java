package tn.esprit.spring.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.resilience.ResilienceTestService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/resilience-test")
@Api(tags = "Tests Resilience4j")
@Slf4j
@CrossOrigin(origins = "*")
public class ResilienceTestController {

    @Autowired
    private ResilienceTestService resilienceTestService;

    /**
     * TEST 1 : Circuit Breaker
     */
    @GetMapping("/circuit-breaker")
    @ApiOperation(value = "Tester Circuit Breaker")
    public ResponseEntity<Map<String, Object>> testCircuitBreaker(
            @RequestParam(defaultValue = "false") boolean fail) {
        
        log.info("=== TEST CIRCUIT BREAKER (fail={}) ===", fail);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = resilienceTestService.testCircuitBreaker(fail);
            response.put("status", "SUCCESS");
            response.put("message", result);
            response.put("pattern", "Circuit Breaker");
            
        } catch (Exception e) {
            response.put("status", "FALLBACK");
            response.put("message", e.getMessage());
            response.put("pattern", "Circuit Breaker");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * TEST 2 : Retry
     */
    @GetMapping("/retry")
    @ApiOperation(value = "Tester Retry (3 tentatives automatiques)")
    public ResponseEntity<Map<String, Object>> testRetry() {
        log.info("=== TEST RETRY ===");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = resilienceTestService.testRetry();
            response.put("status", "SUCCESS");
            response.put("message", result);
            response.put("pattern", "Retry");
            
        } catch (Exception e) {
            response.put("status", "FALLBACK");
            response.put("message", e.getMessage());
            response.put("pattern", "Retry");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * TEST 3 : Rate Limiter
     */
    @GetMapping("/rate-limiter")
    @ApiOperation(value = "Tester Rate Limiter (max 2 requ?tes/seconde)")
    public ResponseEntity<Map<String, Object>> testRateLimiter() {
        log.info("=== TEST RATE LIMITER ===");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = resilienceTestService.testRateLimiter();
            response.put("status", "SUCCESS");
            response.put("message", result);
            response.put("pattern", "Rate Limiter");
            response.put("note", "Limite: 2 requ?tes par seconde");
            
        } catch (Exception e) {
            response.put("status", "RATE_LIMITED");
            response.put("message", "Trop de requ?tes - Limite d?pass?e");
            response.put("pattern", "Rate Limiter");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * TEST 4 : Time Limiter (Timeout)
     */
    @GetMapping("/timeout")
    @ApiOperation(value = "Tester Timeout (max 5 secondes)")
    public ResponseEntity<Map<String, Object>> testTimeout(
            @RequestParam(defaultValue = "3") int delaySeconds) {
        
        log.info("=== TEST TIMEOUT (delay={}s) ===", delaySeconds);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            CompletableFuture<String> future = resilienceTestService.testTimeLimiter(delaySeconds);
            String result = future.get();
            
            response.put("status", "SUCCESS");
            response.put("message", result);
            response.put("pattern", "Time Limiter");
            response.put("delaySeconds", delaySeconds);
            
        } catch (Exception e) {
            response.put("status", "TIMEOUT");
            response.put("message", "Timeout d?pass? (> 5 secondes)");
            response.put("pattern", "Time Limiter");
            response.put("delaySeconds", delaySeconds);
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * TEST 5 : Combinaison (Retry + Circuit Breaker)
     */
    @GetMapping("/combined")
    @ApiOperation(value = "Tester Retry + Circuit Breaker combin?s")
    public ResponseEntity<Map<String, Object>> testCombined(
            @RequestParam(defaultValue = "false") boolean fail) {
        
        log.info("=== TEST COMBINED (fail={}) ===", fail);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            String result = resilienceTestService.testCombined(fail);
            response.put("status", "SUCCESS");
            response.put("message", result);
            response.put("pattern", "Retry + Circuit Breaker");
            
        } catch (Exception e) {
            response.put("status", "FALLBACK");
            response.put("message", e.getMessage());
            response.put("pattern", "Retry + Circuit Breaker");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Informations sur les tests disponibles
     */
    @GetMapping("/info")
    @ApiOperation(value = "Liste des tests Resilience4j disponibles")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("description", "Tests des patterns Resilience4j");
        
        Map<String, String> tests = new HashMap<>();
        tests.put("test_1", "GET /resilience-test/circuit-breaker?fail=true");
        tests.put("test_2", "GET /resilience-test/retry");
        tests.put("test_3", "GET /resilience-test/rate-limiter (appeler 3x rapidement)");
        tests.put("test_4", "GET /resilience-test/timeout?delaySeconds=6");
        tests.put("test_5", "GET /resilience-test/combined?fail=true");
        
        info.put("tests_disponibles", tests);
        
        Map<String, String> patterns = new HashMap<>();
        patterns.put("Circuit Breaker", "Emp?che les appels r?p?t?s ? un service d?faillant");
        patterns.put("Retry", "R?essaye automatiquement (max 3 fois)");
        patterns.put("Rate Limiter", "Limite ? 2 requ?tes/seconde");
        patterns.put("Time Limiter", "Timeout ? 5 secondes");
        
        info.put("patterns_resilience4j", patterns);
        
        return ResponseEntity.ok(info);
    }
}
