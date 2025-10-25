package tn.esprit.spring;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Organisateur;
import tn.esprit.spring.service.classes.OrganisateurService;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:mysql://localhost:3306/gestionevenement",
    "spring.datasource.username=root",
    "spring.datasource.password=0000"
})
@Slf4j
@Transactional
@Rollback
public class OrganisateurServiceImplTest {

    @Autowired
    private OrganisateurService organisateurService;

    @Test
    public void testAddOrganisateur() {
        Organisateur o = new Organisateur();
        o.setNom("Abid");
        o.setPrenom("Nouha");
        o.setEmail("nouha.abid@example.com");

        Organisateur saved = organisateurService.ajouterOrganisateur(o);
        assertNotNull(saved);
        assertTrue(saved.getIdOrg() > 0);
        log.info("✅ Organisateur ajouté avec succès : {}", saved.getNom());
    }

    @Test
    public void testRetrieveOrganisateur() {
        List<Organisateur> list = organisateurService.getAllOrganisateurs();
        log.info("📋 Nombre d’organisateurs trouvés : {}", list.size());
        assertNotNull(list);
    }

    @Test
    public void testUpdateOrganisateur() {
        Organisateur o = new Organisateur();
        o.setNom("Tester");
        o.setPrenom("Update");
        o.setEmail("update@test.com");
        Organisateur saved = organisateurService.ajouterOrganisateur(o);

        saved.setNom("UpdatedName");
        Organisateur updated = organisateurService.modifierOrganisateur(saved);

        assertEquals("UpdatedName", updated.getNom());
        log.info("♻️ Organisateur mis à jour : {}", updated.getNom());
    }

    @Test
    public void testDeleteOrganisateur() {
        Organisateur o = new Organisateur();
        o.setNom("ToDelete");
        o.setPrenom("Temp");
        o.setEmail("delete@temp.com");
        Organisateur saved = organisateurService.ajouterOrganisateur(o);

        organisateurService.supprimerOrganisateur(saved.getIdOrg());
        log.info("🗑️ Organisateur supprimé : {}", saved.getNom());
    }
}
