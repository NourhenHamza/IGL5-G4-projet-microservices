package tn.esprit.spring.service.classes;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.persistence.entities.Tache;
import tn.esprit.spring.persistence.repositories.ParticipantRepository;
import tn.esprit.spring.service.interfaces.IParticipantService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
public class ParticipantServiceImplTest {

    // ✅ This still uses @Autowired because it's a TEST class, not a service
    // Spring Boot tests can use field injection - it's acceptable in test context
    @Autowired
    IParticipantService participantService;

    @Autowired
    ParticipantRepository participantRepository;

    @Test
     void testAddParticipant() throws ParseException {
        log.info("=== Test: Ajout d'un participant ===");

        Participant p = new Participant();
        p.setNom("Ben Ali");
        p.setPrenom("Yasmine");
        p.setTache(Tache.ORGANISATEUR);

        Participant saved = participantService.ajouterParticipant(p);
        log.info("✅ Participant ajouté : ID={}, Nom={}, Prénom={}",
                saved.getIdPart(), saved.getNom(), saved.getPrenom());

        assertNotNull(saved, "Le participant sauvegardé ne doit pas être null");
        assertTrue(saved.getIdPart() > 0, "L'ID doit être généré");
        assertEquals("Yasmine", saved.getPrenom());
        assertEquals("Ben Ali", saved.getNom());
        assertEquals(Tache.ORGANISATEUR, saved.getTache());
    }

    @Test
     void testRetrieveParticipant() {
        log.info("=== Test: Recherche d'un participant ===");

        Participant p = new Participant();
        p.setNom("Chakroun");
        p.setPrenom("Eya");
        p.setTache(Tache.INVITE);
        participantService.ajouterParticipant(p);

        List<Participant> foundList = participantRepository.findByNomAndPrenomAndTache(
                "Chakroun", "Eya", Tache.INVITE
        );

        log.info("✅ Nombre de participants trouvés : {}", foundList.size());

        assertNotNull(foundList, "La liste ne doit pas être null");
        assertFalse(foundList.isEmpty(), "La liste ne devrait pas être vide");

        Participant found = foundList.get(0);
        assertEquals("Chakroun", found.getNom());
        assertEquals("Eya", found.getPrenom());
        assertEquals(Tache.INVITE, found.getTache());

        log.info("✅ Participant trouvé : ID={}, Nom={} {}",
                found.getIdPart(), found.getNom(), found.getPrenom());
    }

    @Test
     void testUpdateParticipant() {
        log.info("=== Test: Mise à jour d'un participant ===");

        Participant p = new Participant();
        p.setNom("Sassi");
        p.setPrenom("Khaled");
        p.setTache(Tache.ORGANISATEUR);
        Participant saved = participantService.ajouterParticipant(p);

        log.info("Participant avant mise à jour : Prénom={}", saved.getPrenom());

        // Mise à jour
        saved.setPrenom("Mohamed");
        participantRepository.save(saved);

        // Vérification
        Participant updated = participantRepository.findById(saved.getIdPart()).orElse(null);
        log.info("✅ Participant après mise à jour : Prénom={}", updated.getPrenom());

        assertNotNull(updated, "Le participant mis à jour ne doit pas être null");
        assertEquals("Mohamed", updated.getPrenom());
        assertEquals("Sassi", updated.getNom());
        assertEquals(Tache.ORGANISATEUR, updated.getTache());
    }

    @Test
     void testDeleteParticipant() {
        log.info("=== Test: Suppression d'un participant ===");

        Participant p = new Participant();
        p.setNom("Hammami");
        p.setPrenom("Sarra");
        p.setTache(Tache.INVITE);
        Participant saved = participantService.ajouterParticipant(p);

        int idToDelete = saved.getIdPart();
        log.info("Participant à supprimer : ID={}, Nom={} {}",
                idToDelete, saved.getNom(), saved.getPrenom());

        // Suppression
        participantRepository.deleteById(idToDelete);

        // Vérification
        boolean exists = participantRepository.existsById(idToDelete);

        log.info("✅ Participant supprimé. Existe encore ? {}", exists);
        assertFalse(exists, "Le participant ne devrait plus exister après suppression");
    }
}