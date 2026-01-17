package tn.esprit.spring.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * Controller fournissant des informations sur le microservice
 * 
 * @RefreshScope permet de recharger la configuration sans redémarrer
 * 
 * @author Votre Nom
 * @version 1.0
 */
@RestController
@RequestMapping("/service")
@Api(tags = "Informations Service", description = "Métadonnées du microservice")
@RefreshScope
public class ServiceInfoController {

    @Value("${spring.application.name:gestion-evenement-service}")
    private String serviceName;

    @Value("${server.port:8082}")
    private String port;

    @Value("${eureka.instance.instance-id:${spring.application.name}:${random.value}}")
    private String instanceId;

    /**
     * Récupérer les informations du microservice
     * 
     * @return Métadonnées du service
     */
    @GetMapping("/info")
    @ApiOperation(value = "Obtenir les informations du microservice")
    public Map<String, Object> getServiceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("serviceName", serviceName);
        info.put("port", port);
        info.put("instanceId", instanceId);
        info.put("status", "UP");
        info.put("description", "Microservice de gestion des événements et de la logistique");
        info.put("version", "1.0.0");
        info.put("features", new String[]{
            "Config Server Integration",
            "Eureka Service Discovery",
            "REST API",
            "Swagger Documentation",
            "Health Checks"
        });
        return info;
    }

    /**
     * Endpoint simple pour tester la connectivité
     * 
     * @return Message de confirmation
     */
    @GetMapping("/ping")
    @ApiOperation(value = "Tester la connectivité du service")
    public Map<String, String> ping() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "pong");
        response.put("service", serviceName);
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }
}