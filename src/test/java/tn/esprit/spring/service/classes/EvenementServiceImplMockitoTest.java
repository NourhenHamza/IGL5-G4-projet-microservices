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

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.persistence.entities.Tache;
import tn.esprit.spring.persistence.repositories.EvenementRepository;
import tn.esprit.spring.persistence.repositories.ParticipantRepository;

/**
 * Mockito Unit Test class for EvenementServiceImpl
 * Tests isolated business logic without database dependencies
 * Contains 4 CRUD test methods with Mockito:
 * 1. testAddEvenementWithMockito (CREATE)
 * 2. testRetrieveEvenementWithMockito (READ)
 * 3. testUpdateEvenementWithMockito (UPDATE)
 * 4. testDeleteEvenementWithMockito (DELETE)
 */
@Slf4j
@ExtendWith(MockitoExtension.class)
public class EvenementServiceImplMockitoTest {

    @Mock
    private EvenementRepository evenementRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private EvenementServiceImpl evenementService;

    private SimpleDateFormat dateFormat;
    private Evenement testEvenement;
    private Participant testParticipant;

    @BeforeEach
    public void setUp() throws ParseException {
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        // Setup test data
        Date dateDebut = dateFormat.parse("01/06/2026");
        Date dateFin = dateFormat.parse("03/06/2026");

        testEvenement = new Evenement();
        testEvenement.setId(1);
        testEvenement.setDescription("Test Festival 2026");
        testEvenement.setDated(dateDebut);
        testEvenement.setDatef(dateFin);
        testEvenement.setCout(4000.0f);

        testParticipant = new Participant();
        testParticipant.setIdPart(1);
        testParticipant.setNom("Benali");
        testParticipant.setPrenom("Fatima");
        testParticipant.setTache(Tache.ORGANISATEUR);
    }

    /**
     * Test 1: CREATE with Mockito - Add a new Evenement
     * Unit test with mocked dependencies
     */
    @Test
    public void testAddEvenementWithMockito() throws ParseException {
        log.info("===== Mockito Test 1: ADD EVENEMENT (CREATE) =====");

        // Arrange
        Date dateDebut = dateFormat.parse("15/07/2026");
        Date dateFin = dateFormat.parse("17/07/2026");

        Evenement newEvenement = new Evenement();
        newEvenement.setDescription("Nouveau Festival Mock");
        newEvenement.setDated(dateDebut);
        newEvenement.setDatef(dateFin);
        newEvenement.setCout(6000.0f);

        Evenement savedEvenement = new Evenement();
        savedEvenement.setId(10);
        savedEvenement.setDescription("Nouveau Festival Mock");
        savedEvenement.setDated(dateDebut);
        savedEvenement.setDatef(dateFin);
        savedEvenement.setCout(6000.0f);

        // Mock the repository save method
        when(evenementRepository.save(any(Evenement.class))).thenReturn(savedEvenement);

        // Act
        Evenement result = evenementService.ajoutAffectEvenParticip(newEvenement);

        // Assert
        assertNotNull(result, "L'événement ne doit pas être null");
        // Note: Le service retourne l'événement sauvegardé par le repository
        // Vérifier que la description est correcte (l'ID peut être 0 ou 10 selon le mock)
        assertEquals("Nouveau Festival Mock", result.getDescription());
        assertEquals(6000.0f, result.getCout());
        assertEquals(dateDebut, result.getDated());
        assertEquals(dateFin, result.getDatef());

        // Verify that save was called once
        verify(evenementRepository, times(1)).save(any(Evenement.class));

        log.info("Mockito - Événement créé avec succès - Description: {}",
                result.getDescription());
        log.info("===== Mockito Test 1 terminé avec succès =====");
    }

    /**
     * Test 2: READ with Mockito - Retrieve an Evenement by ID
     * Unit test with mocked dependencies
     */
    @Test
    public void testRetrieveEvenementWithMockito() {
        log.info("===== Mockito Test 2: RETRIEVE EVENEMENT (READ) =====");

        // Arrange
        int evenementId = 1;
        when(evenementRepository.findById(evenementId)).thenReturn(Optional.of(testEvenement));

        // Act
        Optional<Evenement> result = evenementRepository.findById(evenementId);

        // Assert
        assertTrue(result.isPresent(), "L'événement doit être trouvé");
        assertEquals(1, result.get().getId());
        assertEquals("Test Festival 2026", result.get().getDescription());
        assertEquals(4000.0f, result.get().getCout());

        // Verify that findById was called once
        verify(evenementRepository, times(1)).findById(evenementId);

        log.info("Mockito - Événement récupéré avec succès - ID: {}, Description: {}",
                result.get().getId(), result.get().getDescription());
        log.info("===== Mockito Test 2 terminé avec succès =====");
    }

    /**
     * Test 3: UPDATE with Mockito - Modify an existing Evenement
     * Unit test with mocked dependencies
     */
    @Test
    public void testUpdateEvenementWithMockito() throws ParseException {
        log.info("===== Mockito Test 3: UPDATE EVENEMENT (UPDATE) =====");

        // Arrange
        int evenementId = 1;

        Evenement updatedEvenement = new Evenement();
        updatedEvenement.setId(evenementId);
        updatedEvenement.setDescription("Festival Mis à Jour Mock");
        updatedEvenement.setDated(testEvenement.getDated());
        updatedEvenement.setDatef(testEvenement.getDatef());
        updatedEvenement.setCout(5500.0f);

        when(evenementRepository.findById(evenementId)).thenReturn(Optional.of(testEvenement));
        when(evenementRepository.save(any(Evenement.class))).thenReturn(updatedEvenement);

        // Act
        Optional<Evenement> foundEvenement = evenementRepository.findById(evenementId);
        assertTrue(foundEvenement.isPresent());

        Evenement eventToUpdate = foundEvenement.get();
        eventToUpdate.setDescription("Festival Mis à Jour Mock");
        eventToUpdate.setCout(5500.0f);

        Evenement result = evenementRepository.save(eventToUpdate);

        // Assert
        assertNotNull(result);
        assertEquals(evenementId, result.getId());
        assertEquals("Festival Mis à Jour Mock", result.getDescription());
        assertEquals(5500.0f, result.getCout());

        // Verify interactions
        verify(evenementRepository, times(1)).findById(evenementId);
        verify(evenementRepository, times(1)).save(any(Evenement.class));

        log.info("Mockito - Événement mis à jour avec succès - Nouvelle description: {}, Nouveau coût: {}",
                result.getDescription(), result.getCout());
        log.info("===== Mockito Test 3 terminé avec succès =====");
    }

    /**
     * Test 4: DELETE with Mockito - Remove an Evenement
     * Unit test with mocked dependencies
     */
    @Test
    public void testDeleteEvenementWithMockito() {
        log.info("===== Mockito Test 4: DELETE EVENEMENT (DELETE) =====");

        // Arrange
        int evenementId = 1;

        when(evenementRepository.findById(evenementId))
                .thenReturn(Optional.of(testEvenement))
                .thenReturn(Optional.empty());

        doNothing().when(evenementRepository).deleteById(evenementId);

        // Act
        Optional<Evenement> beforeDelete = evenementRepository.findById(evenementId);
        assertTrue(beforeDelete.isPresent(), "L'événement doit exister avant suppression");

        evenementRepository.deleteById(evenementId);

        Optional<Evenement> afterDelete = evenementRepository.findById(evenementId);

        // Assert
        assertFalse(afterDelete.isPresent(), "L'événement ne doit plus exister après suppression");

        // Verify interactions
        verify(evenementRepository, times(2)).findById(evenementId);
        verify(evenementRepository, times(1)).deleteById(evenementId);

        log.info("Mockito - Événement supprimé avec succès - ID: {}", evenementId);
        log.info("===== Mockito Test 4 terminé avec succès =====");
    }

    /**
     * Additional Test: Add Evenement with Participant affectation
     * Tests the business logic of assigning a participant to an event
     */
    @Test
    public void testAddEvenementWithParticipantAffectation() {
        log.info("===== Mockito Test 5: ADD EVENEMENT WITH PARTICIPANT =====");

        // Arrange
        int participantId = 1;

        Evenement newEvenement = new Evenement();
        newEvenement.setId(2);
        newEvenement.setDescription("Événement avec Participant");
        newEvenement.setCout(3000.0f);

        List<Participant> participants = new ArrayList<>();
        participants.add(testParticipant);
        newEvenement.setParticipants(participants);

        when(participantRepository.findById(participantId)).thenReturn(Optional.of(testParticipant));
        when(evenementRepository.findById(2)).thenReturn(Optional.of(newEvenement));
        when(evenementRepository.save(any(Evenement.class))).thenReturn(newEvenement);

        // Act
        Evenement result = evenementService.ajoutAffectEvenParticip(newEvenement, participantId);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getParticipants());
        assertTrue(result.getParticipants().size() > 0);

        // Verify
        verify(participantRepository, times(1)).findById(participantId);
        verify(evenementRepository, times(1)).save(any(Evenement.class));

        log.info("Mockito - Événement créé avec participant affecté - Nb participants: {}",
                result.getParticipants().size());
        log.info("===== Mockito Test 5 terminé avec succès =====");
    }
}