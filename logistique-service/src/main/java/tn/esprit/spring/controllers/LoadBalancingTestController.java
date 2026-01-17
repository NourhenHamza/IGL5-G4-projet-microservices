package tn.esprit.spring.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import tn.esprit.spring.client.LoadBalancingTestClient;

/**
 * Controller pour tester et démontrer le Load Balancing
 * 
 * Endpoints disponibles:
 * - GET /load-balancing/test/{calls} : Tester le LB avec N appels
 * - GET /load-balancing/instances : Liste des instances disponibles
 * - GET /load-balancing/call : Un seul appel via LB
 * - GET /load-balancing/current-instance : Info de l'instance actuelle
 * 
 * @author Votre Nom
 * @version 1.0
 */
@RestController
@RequestMapping("/load-balancing")
@Api(tags = "Load Balancing Tests", description = "Endpoints pour démontrer le Load Balancing")
@CrossOrigin(origins = "*")
public class LoadBalancingTestController {

    @Autowired
    private LoadBalancingTestClient loadBalancingClient;

    @Value("${server.port}")
    private String currentPort;

    @Value("${spring.application.name}")
    private String serviceName;

    /**
     * Tester le Load Balancing avec un nombre spécifique d'appels
     * 
     * @param numberOfCalls Nombre d'appels à effectuer
     * @return Statistiques de répartition
     */
    @GetMapping("/test/{numberOfCalls}")
    @ApiOperation(value = "Tester le Load Balancing avec N appels")
    public ResponseEntity<Map<String, Object>> testLoadBalancing(
            @PathVariable("numberOfCalls") int numberOfCalls) {
        
        if (numberOfCalls < 1 || numberOfCalls > 100) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Le nombre d'appels doit être entre 1 et 100");
            return ResponseEntity.badRequest().body(error);
        }

        Map<String, Object> stats = loadBalancingClient.testLoadBalancing(numberOfCalls);
        return ResponseEntity.ok(stats);
    }

    /**
     * Obtenir la liste des instances disponibles dans Eureka
     * 
     * @return Liste des instances
     */
    @GetMapping("/instances")
    @ApiOperation(value = "Lister toutes les instances disponibles")
    public ResponseEntity<Map<String, Object>> getInstances() {
        List<ServiceInstance> instances = loadBalancingClient.getAvailableInstances();
        
        Map<String, Object> response = new HashMap<>();
        response.put("serviceName", serviceName);
        response.put("totalInstances", instances.size());
        response.put("instances", instances);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Effectuer un seul appel via Load Balancer
     * 
     * @return Réponse du service appelé
     */
    @GetMapping("/call")
    @ApiOperation(value = "Effectuer un appel via Load Balancer")
    public ResponseEntity<Map<String, Object>> callViaLoadBalancer() {
        Map<String, Object> response = loadBalancingClient.callServiceViaLoadBalancer();
        return ResponseEntity.ok(response);
    }

    /**
     * Obtenir les informations de l'instance actuelle
     * 
     * @return Info de cette instance
     */
    @GetMapping("/current-instance")
    @ApiOperation(value = "Obtenir les infos de l'instance actuelle")
    public ResponseEntity<Map<String, Object>> getCurrentInstanceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("serviceName", serviceName);
        info.put("port", currentPort);
        info.put("message", "Vous êtes connecté à l'instance sur le port " + currentPort);
        info.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(info);
    }
}