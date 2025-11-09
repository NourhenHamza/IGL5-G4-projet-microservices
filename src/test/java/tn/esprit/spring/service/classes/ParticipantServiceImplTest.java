package tn.esprit.spring.service.classes;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.persistence.entities.Tache;
import tn.esprit.spring.persistence.repositories.ParticipantRepository;
import tn.esprit.spring.service.interfaces.IParticipantService;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.properties")
@Transactional
@Slf4j
public class ParticipantServiceImplTest {

    @Autowired
    IParticipantService participantService;

    @Autowired
    ParticipantRepository participantRepository;

    @Test
    public void testAddParticipant() throws ParseException {
        Participant p = new Participant();
        p.setNom("Ben Ali");
        p.setPrenom("Yasmine");
        p.setTache(Tache.ORGANISATEUR);

        Participant saved = participantService.ajouterParticipant(p);
        log.info("✅ Participant ajouté : {}", saved);

        assertNotNull(saved);
        assertTrue(saved.getIdPart() > 0);
        assertEquals("Yasmine", saved.getPrenom());
    }

    @Test
    public void testRetrieveParticipant() {
        Participant p = new Participant();
        p.setNom("Chakroun");
        p.setPrenom("Eya");
        p.setTache(Tache.INVITE);
        participantService.ajouterParticipant(p);

        // Changed to return List instead of single Participant
        List<Participant> foundList = participantRepository.findByNomAndPrenomAndTache("Chakroun", "Eya", Tache.INVITE);
        log.info("✅ Participants trouvés : {}", foundList.size());

        assertNotNull(foundList);
        assertFalse(foundList.isEmpty(), "La liste ne devrait pas être vide");

        // Get the first participant from the list
        Participant found = foundList.get(0);
        assertEquals("Chakroun", found.getNom());
        assertEquals("Eya", found.getPrenom());
        assertEquals(Tache.INVITE, found.getTache());

        log.info("✅ Premier participant trouvé : {}", found);
    }

    @Test
    public void testUpdateParticipant() {
        Participant p = new Participant();
        p.setNom("Sassi");
        p.setPrenom("Khaled");
        p.setTache(Tache.ORGANISATEUR);
        Participant saved = participantService.ajouterParticipant(p);

        saved.setPrenom("Mohamed");
        participantRepository.save(saved);

        Participant updated = participantRepository.findById(saved.getIdPart()).orElse(null);
        log.info("✅ Participant mis à jour : {}", updated);

        assertNotNull(updated);
        assertEquals("Mohamed", updated.getPrenom());
    }

    @Test
    public void testDeleteParticipant() {
        Participant p = new Participant();
        p.setNom("Hammami");
        p.setPrenom("Sarra");
        p.setTache(Tache.INVITE);
        Participant saved = participantService.ajouterParticipant(p);

        participantRepository.deleteById(saved.getIdPart());
        boolean exists = participantRepository.existsById(saved.getIdPart());

        log.info("✅ Participant supprimé : {}", saved);
        assertFalse(exists);
    }
}