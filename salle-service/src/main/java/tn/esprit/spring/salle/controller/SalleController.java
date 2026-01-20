package tn.esprit.spring.salle.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.salle.dto.SalleDTO;
import tn.esprit.spring.salle.entity.Salle;
import tn.esprit.spring.salle.mapper.SalleMapper;
import tn.esprit.spring.salle.service.SalleService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/salles")
@CrossOrigin(origins = "http://localhost:8080")
@RequiredArgsConstructor
@Slf4j
public class SalleController {

    private final SalleService salleService;

    /**
     * Get all salles with Resilience4j patterns
     */
    @GetMapping
    @Retry(name = "salleRetry", fallbackMethod = "fallbackGetAllSalles")
    @RateLimiter(name = "salleRateLimiter", fallbackMethod = "fallbackRateLimitExceeded")
    @CircuitBreaker(name = "salleMicroservice", fallbackMethod = "fallbackGetAllSalles")
    public ResponseEntity<List<SalleDTO>> getAllSalles() {
        log.info("🔵 Request to get all salles");
        List<Salle> salles = salleService.getAllSalles();
        List<SalleDTO> dtos = salles.stream()
                .map(SalleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get salle by ID
     */
    @GetMapping("/{id}")
    @CircuitBreaker(name = "salleMicroservice", fallbackMethod = "fallbackGetSalle")
    public ResponseEntity<SalleDTO> getSalleById(@PathVariable Integer id) {
        log.info("🔵 Request to get salle with id: {}", id);
        return salleService.getSalleById(id)
                .map(salle -> ResponseEntity.ok(SalleMapper.toDTO(salle)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Add new salle
     */
    @PostMapping
    public ResponseEntity<SalleDTO> ajouterSalle(@RequestBody SalleDTO salleDTO) {
        log.info("🔵 Request to create salle: {}", salleDTO.getNom());
        Salle salle = SalleMapper.toEntity(salleDTO);
        Salle savedSalle = salleService.ajouterSalle(salle);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SalleMapper.toDTO(savedSalle));
    }

    /**
     * Update salle
     */
    @PutMapping
    public ResponseEntity<SalleDTO> modifierSalle(@RequestBody SalleDTO salleDTO) {
        log.info("🔵 Request to update salle with id: {}", salleDTO.getIdSalle());
        Salle salle = SalleMapper.toEntity(salleDTO);
        Salle updatedSalle = salleService.modifierSalle(salle);
        return ResponseEntity.ok(SalleMapper.toDTO(updatedSalle));
    }

    /**
     * Delete salle
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerSalle(@PathVariable Integer id) {
        log.info("🔵 Request to delete salle with id: {}", id);
        salleService.supprimerSalle(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get available salles
     */
    @GetMapping("/disponibles")
    @CircuitBreaker(name = "salleMicroservice", fallbackMethod = "fallbackGetAllSalles")
    public ResponseEntity<List<SalleDTO>> getSallesDisponibles() {
        log.info("🔵 Request to get available salles");
        List<Salle> salles = salleService.getSallesDisponibles();
        List<SalleDTO> dtos = salles.stream()
                .map(SalleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Get salles by minimum capacity
     */
    @GetMapping("/capacite/{min}")
    @CircuitBreaker(name = "salleMicroservice", fallbackMethod = "fallbackGetAllSalles")
    public ResponseEntity<List<SalleDTO>> getSallesParCapacite(@PathVariable Integer min) {
        log.info("🔵 Request to get salles with capacity >= {}", min);
        List<Salle> salles = salleService.getSallesParCapaciteMin(min);
        List<SalleDTO> dtos = salles.stream()
                .map(SalleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Reserve salle
     */
    @PutMapping("/reserver/{id}")
    @CircuitBreaker(name = "salleMicroservice", fallbackMethod = "fallbackReserverSalle")
    public ResponseEntity<SalleDTO> reserverSalle(@PathVariable Integer id) {
        log.info("🔵 Request to reserve salle with id: {}", id);
        Salle salle = salleService.reserverSalle(id);
        return ResponseEntity.ok(SalleMapper.toDTO(salle));
    }

    /**
     * Free salle
     */
    @PutMapping("/liberer/{id}")
    @CircuitBreaker(name = "salleMicroservice", fallbackMethod = "fallbackLibererSalle")
    public ResponseEntity<SalleDTO> libererSalle(@PathVariable Integer id) {
        log.info("🔵 Request to free salle with id: {}", id);
        Salle salle = salleService.libererSalle(id);
        return ResponseEntity.ok(SalleMapper.toDTO(salle));
    }

    // ==================== FALLBACK METHODS ====================

    /**
     * Fallback for getAllSalles
     */
    public ResponseEntity<List<SalleDTO>> fallbackGetAllSalles(Exception e) {
        log.error("🔴 Fallback triggered for getAllSalles: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Collections.emptyList());
    }

    /**
     * Fallback for rate limiter
     */
    public ResponseEntity<List<SalleDTO>> fallbackRateLimitExceeded(Exception e) {
        log.error("🔴 Rate limit exceeded: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Collections.emptyList());
    }

    /**
     * Fallback for getSalle
     */
    public ResponseEntity<SalleDTO> fallbackGetSalle(Integer id, Exception e) {
        log.error("🔴 Fallback triggered for getSalle with id {}: {}", id, e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    /**
     * Fallback for reserverSalle
     */
    public ResponseEntity<SalleDTO> fallbackReserverSalle(Integer id, Exception e) {
        log.error("🔴 Fallback triggered for reserverSalle with id {}: {}", id, e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    /**
     * Fallback for libererSalle
     */
    public ResponseEntity<SalleDTO> fallbackLibererSalle(Integer id, Exception e) {
        log.error("🔴 Fallback triggered for libererSalle with id {}: {}", id, e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}