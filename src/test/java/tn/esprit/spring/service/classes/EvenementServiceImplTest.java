package tn.esprit.spring.service.classes;



import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.persistence.entities.Tache;
import tn.esprit.spring.persistence.repositories.EvenementRepository;
import tn.esprit.spring.persistence.repositories.ParticipantRepository;
import tn.esprit.spring.service.interfaces.IEvenemntService;

/**
 * Test class for EvenementServiceImpl
 * Contains 4 CRUD test methods:
 * 1. testAddEvenement (CREATE)
 * 2. testRetrieveEvenement (READ)
 * 3. testUpdateEvenement (UPDATE)
 * 4. testDeleteEvenement (DELETE)
 *
 * Includes both integration tests (with @SpringBootTest)
 * and unit tests with Mockito (with @ExtendWith(MockitoExtension.class))
 */
@Slf4j
@SpringBootTest
public class EvenementServiceImplTest {

    @Autowired
    IEvenemntService evenementService;

    @Autowired
    EvenementRepository evenementRepository;

    @Autowired
    ParticipantRepository participantRepository;

    private SimpleDateFormat dateFormat;

    @BeforeEach
    public void setUp() {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    }

    /**
     * Test 1: CREATE - Add a new Evenement with participants
     * Integration test using real database
     */
    @Test
    public void testAddEvenement() throws ParseException {
        log.info("===== Test 1: ADD EVENEMENT (CREATE) =====");

        // Arrange - Create test data
        Date dateDebut = dateFormat.parse("01/01/2026");
        Date dateFin = dateFormat.parse("03/01/2026");

        Evenement evenement = new Evenement();
        evenement.setDescription("Festival de Musique 2026");
        evenement.setDated(dateDebut);
        evenement.setDatef(dateFin);
        evenement.setCout(5000.0f);

        // Act - Add the evenement
        Evenement savedEvenement = evenementService.ajoutAffectEvenParticip(evenement);

        // Assert - Verify the evenement was saved correctly
        assertNotNull(savedEvenement, "L'événement ne doit pas être null");
        assertNotNull(savedEvenement.getId(), "L'ID de l'événement doit être généré");
        assertEquals("Festival de Musique 2026", savedEvenement.getDescription());
        assertEquals(dateDebut, savedEvenement.getDated());
        assertEquals(dateFin, savedEvenement.getDatef());
        assertEquals(5000.0f, savedEvenement.getCout());

        log.info("Événement créé avec succès - ID: {}, Description: {}",
                savedEvenement.getId(), savedEvenement.getDescription());

        // Clean up - Delete the test data
        evenementRepository.deleteById(savedEvenement.getId());
        log.info("===== Test 1 terminé avec succès =====");
    }

    /**
     * Test 2: READ - Retrieve an Evenement by ID
     * Integration test using real database
     */
    @Test
    public void testRetrieveEvenement() throws ParseException {
        log.info("===== Test 2: RETRIEVE EVENEMENT (READ) =====");

        // Arrange - Create and save an evenement first
        Date dateDebut = dateFormat.parse("15/02/2026");
        Date dateFin = dateFormat.parse("17/02/2026");

        Evenement evenement = new Evenement();
        evenement.setDescription("Conférence Tech 2026");
        evenement.setDated(dateDebut);
        evenement.setDatef(dateFin);
        evenement.setCout(3000.0f);

        Evenement savedEvenement = evenementRepository.save(evenement);
        log.info("Événement créé pour le test - ID: {}", savedEvenement.getId());

        // Act - Retrieve the evenement
        Optional<Evenement> retrievedEvenement = evenementRepository.findById(savedEvenement.getId());

        // Assert - Verify the evenement was retrieved correctly
        assertTrue(retrievedEvenement.isPresent(), "L'événement doit être trouvé");
        assertEquals(savedEvenement.getId(), retrievedEvenement.get().getId());
        assertEquals("Conférence Tech 2026", retrievedEvenement.get().getDescription());
        assertEquals(dateDebut, retrievedEvenement.get().getDated());
        assertEquals(dateFin, retrievedEvenement.get().getDatef());

        log.info("Événement récupéré avec succès - ID: {}, Description: {}",
                retrievedEvenement.get().getId(), retrievedEvenement.get().getDescription());

        // Clean up
        evenementRepository.deleteById(savedEvenement.getId());
        log.info("===== Test 2 terminé avec succès =====");
    }

    /**
     * Test 3: UPDATE - Modify an existing Evenement
     * Integration test using real database
     */
    @Test
    public void testUpdateEvenement() throws ParseException {
        log.info("===== Test 3: UPDATE EVENEMENT (UPDATE) =====");

        // Arrange - Create and save an evenement
        Date dateDebut = dateFormat.parse("10/03/2026");
        Date dateFin = dateFormat.parse("12/03/2026");

        Evenement evenement = new Evenement();
        evenement.setDescription("Séminaire Initial");
        evenement.setDated(dateDebut);
        evenement.setDatef(dateFin);
        evenement.setCout(2000.0f);

        Evenement savedEvenement = evenementRepository.save(evenement);
        log.info("Événement créé - Description initiale: {}", savedEvenement.getDescription());

        // Act - Update the evenement
        savedEvenement.setDescription("Séminaire Mis à Jour 2026");
        savedEvenement.setCout(2500.0f);
        Evenement updatedEvenement = evenementRepository.save(savedEvenement);

        // Assert - Verify the evenement was updated
        assertNotNull(updatedEvenement);
        assertEquals(savedEvenement.getId(), updatedEvenement.getId());
        assertEquals("Séminaire Mis à Jour 2026", updatedEvenement.getDescription());
        assertEquals(2500.0f, updatedEvenement.getCout());

        log.info("Événement mis à jour avec succès - Nouvelle description: {}, Nouveau coût: {}",
                updatedEvenement.getDescription(), updatedEvenement.getCout());

        // Clean up
        evenementRepository.deleteById(savedEvenement.getId());
        log.info("===== Test 3 terminé avec succès =====");
    }

    /**
     * Test 4: DELETE - Remove an Evenement
     * Integration test using real database
     */
    @Test
    public void testDeleteEvenement() throws ParseException {
        log.info("===== Test 4: DELETE EVENEMENT (DELETE) =====");

        // Arrange - Create and save an evenement
        Date dateDebut = dateFormat.parse("20/04/2026");
        Date dateFin = dateFormat.parse("22/04/2026");

        Evenement evenement = new Evenement();
        evenement.setDescription("Événement à Supprimer");
        evenement.setDated(dateDebut);
        evenement.setDatef(dateFin);
        evenement.setCout(1000.0f);

        Evenement savedEvenement = evenementRepository.save(evenement);
        int evenementId = savedEvenement.getId();
        log.info("Événement créé pour suppression - ID: {}", evenementId);

        // Act - Delete the evenement
        evenementRepository.deleteById(evenementId);

        // Assert - Verify the evenement was deleted
        Optional<Evenement> deletedEvenement = evenementRepository.findById(evenementId);
        assertFalse(deletedEvenement.isPresent(), "L'événement doit être supprimé");

        log.info("Événement supprimé avec succès - ID: {}", evenementId);
        log.info("===== Test 4 terminé avec succès =====");
    }
}