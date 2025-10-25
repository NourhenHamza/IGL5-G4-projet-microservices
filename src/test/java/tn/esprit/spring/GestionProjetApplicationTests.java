package tn.esprit.spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:mysql://localhost:3306/gestionevenement",
    "spring.datasource.username=root",
    "spring.datasource.password=0000"
})
class GestionProjetApplicationTests {

	@Test
	void contextLoads() {
	}

}
