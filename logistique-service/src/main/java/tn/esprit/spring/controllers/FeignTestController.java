package tn.esprit.spring.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.client.EvenementClient;
import tn.esprit.spring.dto.EvenementDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feign-test")
@Api(tags = "Test Feign Client")
@Slf4j
@CrossOrigin(origins = "*")
public class FeignTestController {

    @Autowired
    private EvenementClient evenementClient;

    @GetMapping("/test-service-not-found")
    @ApiOperation(value = "Simuler : Service non trouve dans Eureka")
    public ResponseEntity<Map<String, Object>> testServiceNotFound() {
        log.info("TEST 1 : Appel a un service inexistant");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<EvenementDTO> evenements = evenementClient.getAllEvenements();
            
            response.put("status", "FALLBACK");
            response.put("message", "Service evenement-service non trouve dans Eureka");
            response.put("data", evenements);
            response.put("error_type", "SERVICE_NOT_FOUND");
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/test-timeout/{id}")
    @ApiOperation(value = "Simuler : Timeout de reponse")
    public ResponseEntity<Map<String, Object>> testTimeout(@PathVariable Long id) {
        log.info("TEST 2 : Appel avec risque de timeout (ID: {})", id);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            EvenementDTO evenement = evenementClient.getEvenementById(id);
            
            response.put("status", "FALLBACK");
            response.put("message", "Timeout depasse");
            response.put("data", evenement);
            response.put("error_type", "TIMEOUT");
            
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/test-404")
    @ApiOperation(value = "Simuler : Endpoint inexistant (404)")
    public ResponseEntity<Map<String, Object>> test404(@RequestBody EvenementDTO evenementDTO) {
        log.info("TEST 3 : Appel a un endpoint inexistant");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            EvenementDTO result = evenementClient.createEvenement(evenementDTO);
            
            response.put("status", "FALLBACK");
            response.put("message", "Endpoint inexistant (404)");
            response.put("data", result);
            response.put("error_type", "NOT_FOUND_404");
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/info")
    @ApiOperation(value = "Informations sur les tests Feign disponibles")
    public ResponseEntity<Map<String, Object>> getTestInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("description", "Tests de communication inter-services avec Feign Client");
        info.put("service_cible", "evenement-service (FICTIF)");
        
        Map<String, String> tests = new HashMap<>();
        tests.put("test_1", "GET /feign-test/test-service-not-found");
        tests.put("test_2", "GET /feign-test/test-timeout/{id}");
        tests.put("test_3", "POST /feign-test/test-404");
        
        info.put("tests_disponibles", tests);
        
        return ResponseEntity.ok(info);
    }
}