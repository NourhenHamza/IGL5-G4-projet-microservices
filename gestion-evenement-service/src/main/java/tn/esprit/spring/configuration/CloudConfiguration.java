package tn.esprit.spring.configuration;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration Spring Cloud pour le microservice
 * 
 * Fonctionnalités :
 * - RestTemplate avec Load Balancing
 * - Communication inter-microservices via Eureka
 * 
 * @author Votre Nom
 * @version 1.0
 */
@Configuration
public class CloudConfiguration {

    /**
     * RestTemplate avec Load Balancing activé
     * Permet la communication avec d'autres microservices via leurs noms Eureka
     * 
     * Exemple d'utilisation :
     * restTemplate.getForObject("http://AUTRE-SERVICE/api/endpoint", String.class)
     * 
     * @return RestTemplate configuré
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}