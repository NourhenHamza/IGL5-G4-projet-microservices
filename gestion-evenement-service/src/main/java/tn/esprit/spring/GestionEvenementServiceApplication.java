package tn.esprit.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Application principale du microservice Gestion Événement
 * 
 * Fonctionnalités Spring Cloud activées :
 * - @EnableDiscoveryClient : Enregistrement auprès d'Eureka Server
 * - Config Client : Récupération de la configuration depuis Config Server
 * 
 * @author Votre Nom
 * @version 1.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GestionEvenementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionEvenementServiceApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  Service Gestion Evenement demarre !");
        System.out.println("  - Enregistre aupres d'Eureka Server");
        System.out.println("  - Configuration depuis Config Server");
        System.out.println("  - Module Logistique actif");
        System.out.println("==============================================");
    }
}