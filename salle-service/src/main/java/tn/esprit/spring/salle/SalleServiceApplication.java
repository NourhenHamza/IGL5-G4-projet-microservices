package tn.esprit.spring.salle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SalleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalleServiceApplication.class, args);
        System.out.println("============================================");
        System.out.println("🏢 Salle Service démarré avec succès!");
        System.out.println("============================================");
    }
}