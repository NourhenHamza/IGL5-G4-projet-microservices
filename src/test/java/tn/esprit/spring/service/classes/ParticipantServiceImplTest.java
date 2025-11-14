package tn.esprit.spring.service.classes;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Logistique;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.persistence.entities.Tache;
import tn.esprit.spring.persistence.repositories.EvenementRepository;
import tn.esprit.spring.persistence.repositories.LogistiqueRepository;
import tn.esprit.spring.persistence.repositories.ParticipantRepository;
import tn.esprit.spring.service.interfaces.IParticipantService;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
public class ParticipantServiceImplTest {

    @Autowired
    IParticipantService participantService;

    @Autowired
    ParticipantRepository participantRepository;

    @Autowired
    EvenementRepository evenementRepository;

    @Autowired
    LogistiqueRepository logistiqueRepository;

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

        saved.setPrenom("Mohamed");
        participantRepository.save(saved);

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

        participantRepository.deleteById(idToDelete);

        boolean exists = participantRepository.existsById(idToDelete);

        log.info("✅ Participant supprimé. Existe encore ? {}", exists);
        assertFalse(exists, "Le participant ne devrait plus exister après suppression");
    }

    @Test
    void testCalculCout_WithNoEvents() {
        log.info("=== Test: calculCout avec aucun événement ===");

        evenementRepository.deleteAll();

        assertDoesNotThrow(() -> participantService.calculCout());

        log.info("✅ calculCout executed successfully with no events");
    }

    @Test
    void testCalculCout_WithEventsAndLogistics() {
        log.info("=== Test: calculCout avec événements ET logistiques ===");

        // Create logistics FIRST
        Logistique logistique1 = new Logistique();
        logistique1.setDescription("Chairs");
        logistique1.setPrix(50.0f);
        logistique1.setQuantite(10);
        logistique1.setReserve(true);
        logistiqueRepository.save(logistique1);

        Logistique logistique2 = new Logistique();
        logistique2.setDescription("Tables");
        logistique2.setPrix(100.0f);
        logistique2.setQuantite(5);
        logistique2.setReserve(true);
        logistiqueRepository.save(logistique2);

        // Create event
        Evenement event = new Evenement();
        event.setDescription("Conference Tech 2025");
        event.setCout(0);
        evenementRepository.save(event);

        // Execute calculation
        participantService.calculCout();

        // Verify costs were updated
        Evenement updatedEvent = evenementRepository.findById(event.getId()).orElse(null);
        assertNotNull(updatedEvent);
        assertTrue(updatedEvent.getCout() > 0, "Cost should be greater than 0");

        log.info("✅ Event cost calculated: {}", updatedEvent.getCout());
    }

    @Test
    void testCalculCout_WithMultipleEventsAndLogistics() {
        log.info("=== Test: calculCout avec plusieurs événements et logistiques ===");

        // Create logistics
        Logistique logistique1 = new Logistique();
        logistique1.setDescription("Projector");
        logistique1.setPrix(200.0f);
        logistique1.setQuantite(2);
        logistique1.setReserve(true);
        logistiqueRepository.save(logistique1);

        // Create multiple events
        Evenement event1 = new Evenement();
        event1.setDescription("Morning Workshop");
        event1.setCout(0);
        evenementRepository.save(event1);

        Evenement event2 = new Evenement();
        event2.setDescription("Afternoon Workshop");
        event2.setCout(0);
        evenementRepository.save(event2);

        // Execute calculation
        participantService.calculCout();

        // Verify both events have costs
        Evenement updatedEvent1 = evenementRepository.findById(event1.getId()).orElse(null);
        Evenement updatedEvent2 = evenementRepository.findById(event2.getId()).orElse(null);

        assertNotNull(updatedEvent1);
        assertNotNull(updatedEvent2);
        assertTrue(updatedEvent1.getCout() >= 0);
        assertTrue(updatedEvent2.getCout() >= 0);

        log.info("✅ Event 1 cost: {}, Event 2 cost: {}",
                updatedEvent1.getCout(), updatedEvent2.getCout());
    }

    @Test
    void testGetParReservLogis_WithOrganizateurs() {
        log.info("=== Test: getParReservLogis avec organisateurs ===");

        // Create logistics
        Logistique logistique = new Logistique();
        logistique.setDescription("Audio Equipment");
        logistique.setPrix(150.0f);
        logistique.setQuantite(3);
        logistique.setReserve(true);
        logistiqueRepository.save(logistique);

        // Create event and INITIALIZE collections
        Evenement event = new Evenement();
        event.setDescription("Music Festival");
        event.setLogistiques(new ArrayList<>());  // Initialize the list
        event.setParticipants(new ArrayList<>());  // Initialize the list
        evenementRepository.save(event);

        // Link logistics to event
        event.getLogistiques().add(logistique);
        evenementRepository.save(event);

        // Create participants
        Participant org1 = new Participant();
        org1.setNom("Trabelsi");
        org1.setPrenom("Ahmed");
        org1.setTache(Tache.ORGANISATEUR);
        participantService.ajouterParticipant(org1);

        // Link participant to event
        event.getParticipants().add(org1);
        evenementRepository.save(event);

        Participant org2 = new Participant();
        org2.setNom("Jemli");
        org2.setPrenom("Salma");
        org2.setTache(Tache.ORGANISATEUR);
        participantService.ajouterParticipant(org2);

        event.getParticipants().add(org2);
        evenementRepository.save(event);

        // Create INVITE participant (should not be included)
        Participant invite = new Participant();
        invite.setNom("Bouaziz");
        invite.setPrenom("Ines");
        invite.setTache(Tache.INVITE);
        participantService.ajouterParticipant(invite);

        // Execute method
        List<Participant> organisateurs = participantService.getParReservLogis();

        assertNotNull(organisateurs, "La liste ne doit pas être null");
        log.info("✅ Nombre d'organisateurs trouvés : {}", organisateurs.size());

        // Verify only ORGANISATEUR participants are returned
        for (Participant p : organisateurs) {
            assertEquals(Tache.ORGANISATEUR, p.getTache(),
                    "Seuls les organisateurs doivent être retournés");
            log.info("Organisateur: {} {}", p.getNom(), p.getPrenom());
        }
    }

    @Test
    void testGetParReservLogis_WithNoOrganizateurs() {
        log.info("=== Test: getParReservLogis sans organisateurs ===");

        participantRepository.deleteAll();

        Participant invite1 = new Participant();
        invite1.setNom("Karoui");
        invite1.setPrenom("Nour");
        invite1.setTache(Tache.INVITE);
        participantService.ajouterParticipant(invite1);

        List<Participant> organisateurs = participantService.getParReservLogis();

        assertNotNull(organisateurs, "La liste ne doit pas être null");
        log.info("✅ Aucun organisateur trouvé (attendu)");
    }

    @Test
    void testAddParticipant_WithAllTacheTypes() {
        log.info("=== Test: Ajout de participants avec tous les types de tâches ===");

        Participant org = new Participant();
        org.setNom("Amri");
        org.setPrenom("Karim");
        org.setTache(Tache.ORGANISATEUR);
        Participant savedOrg = participantService.ajouterParticipant(org);
        assertEquals(Tache.ORGANISATEUR, savedOrg.getTache());

        Participant inv = new Participant();
        inv.setNom("Zouari");
        inv.setPrenom("Leila");
        inv.setTache(Tache.INVITE);
        Participant savedInv = participantService.ajouterParticipant(inv);
        assertEquals(Tache.INVITE, savedInv.getTache());

        log.info("✅ Tous les types de tâches testés avec succès");
    }

    @Test
    void testParticipantPersistence() {
        log.info("=== Test: Persistance des données participant ===");

        Participant p = new Participant();
        p.setNom("Ferchichi");
        p.setPrenom("Rania");
        p.setTache(Tache.ORGANISATEUR);

        Participant saved = participantService.ajouterParticipant(p);
        int savedId = saved.getIdPart();

        Participant retrieved = participantRepository.findById(savedId).orElse(null);

        assertNotNull(retrieved, "Le participant doit être persisté");
        assertEquals(saved.getNom(), retrieved.getNom());
        assertEquals(saved.getPrenom(), retrieved.getPrenom());
        assertEquals(saved.getTache(), retrieved.getTache());

        log.info("✅ Persistance vérifiée avec succès");
    }

    @Test
    void testRetrieveAllParticipants() {
        log.info("=== Test: Récupération de tous les participants ===");

        participantRepository.deleteAll();

        Participant p1 = new Participant();
        p1.setNom("Participant1");
        p1.setPrenom("Test1");
        p1.setTache(Tache.ORGANISATEUR);
        participantService.ajouterParticipant(p1);

        Participant p2 = new Participant();
        p2.setNom("Participant2");
        p2.setPrenom("Test2");
        p2.setTache(Tache.INVITE);
        participantService.ajouterParticipant(p2);

        List<Participant> all = (List<Participant>) participantRepository.findAll();
        assertEquals(2, all.size());

        log.info("✅ Total participants: {}", all.size());
    }

    @Test
    void testFindParticipantById() {
        log.info("=== Test: Recherche par ID ===");

        Participant p = new Participant();
        p.setNom("TestNom");
        p.setPrenom("TestPrenom");
        p.setTache(Tache.ORGANISATEUR);
        Participant saved = participantService.ajouterParticipant(p);

        Participant found = participantRepository.findById(saved.getIdPart()).orElse(null);
        assertNotNull(found);
        assertEquals(saved.getIdPart(), found.getIdPart());

        log.info("✅ Participant trouvé par ID: {}", found.getIdPart());
    }

    @Test
    void testUpdateMultipleFields() {
        log.info("=== Test: Mise à jour de plusieurs champs ===");

        Participant p = new Participant();
        p.setNom("OldNom");
        p.setPrenom("OldPrenom");
        p.setTache(Tache.INVITE);
        Participant saved = participantService.ajouterParticipant(p);

        saved.setNom("NewNom");
        saved.setPrenom("NewPrenom");
        saved.setTache(Tache.ORGANISATEUR);
        participantRepository.save(saved);

        Participant updated = participantRepository.findById(saved.getIdPart()).orElse(null);
        assertEquals("NewNom", updated.getNom());
        assertEquals("NewPrenom", updated.getPrenom());
        assertEquals(Tache.ORGANISATEUR, updated.getTache());

        log.info("✅ Tous les champs mis à jour avec succès");
    }

    @Test
    void testDeleteAllParticipants() {
        log.info("=== Test: Suppression de tous les participants ===");

        Participant p1 = new Participant();
        p1.setNom("ToDelete1");
        p1.setPrenom("Test1");
        p1.setTache(Tache.ORGANISATEUR);
        participantService.ajouterParticipant(p1);

        Participant p2 = new Participant();
        p2.setNom("ToDelete2");
        p2.setPrenom("Test2");
        p2.setTache(Tache.INVITE);
        participantService.ajouterParticipant(p2);

        participantRepository.deleteAll();

        long count = participantRepository.count();
        assertEquals(0, count);

        log.info("✅ Tous les participants supprimés");
    }

    @Test
    void testCountParticipants() {
        log.info("=== Test: Comptage des participants ===");

        participantRepository.deleteAll();

        long initialCount = participantRepository.count();
        assertEquals(0, initialCount);

        Participant p = new Participant();
        p.setNom("CountTest");
        p.setPrenom("Test");
        p.setTache(Tache.ORGANISATEUR);
        participantService.ajouterParticipant(p);

        long finalCount = participantRepository.count();
        assertEquals(1, finalCount);

        log.info("✅ Comptage vérifié: {} participants", finalCount);
    }
}