package tn.esprit.spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@ActiveProfiles("test")
@Slf4j
class GestionProjetApplicationTests {

    @Test
    void contextLoads() {
        log.info("✅ Spring Boot Application Context chargé avec succès!");
        // Ce test vérifie simplement que le contexte Spring se charge correctement
        // avec la nouvelle configuration (constructor injection)
    }

}