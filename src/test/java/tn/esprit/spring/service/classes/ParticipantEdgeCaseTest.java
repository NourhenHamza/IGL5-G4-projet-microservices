package tn.esprit.spring.service.classes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.persistence.entities.Tache;
import tn.esprit.spring.persistence.repositories.EvenementRepository;
import tn.esprit.spring.persistence.repositories.LogistiqueRepository;
import tn.esprit.spring.persistence.repositories.ParticipantRepository;

@Slf4j
public class ParticipantEdgeCaseTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EvenementRepository eventRepository;

    @Mock
    private LogistiqueRepository logistiqueRepository;

    @InjectMocks
    private ParticipantServiceImpl participantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ========================================
    // 🎯 CRITICAL: Tests for BRANCH coverage
    // ========================================

    @Test
    void testCalculCout_EmptyEventsList_ShouldHandleGracefully() {
        log.info("=== Test: calculCout with empty events list (BRANCH TEST) ===");

        // Mock empty list - tests the if(evenements.isEmpty()) branch
        List<Evenement> emptyList = new ArrayList<>();
        when(eventRepository.findAll()).thenReturn(emptyList);

        // Should NOT throw exception
        assertDoesNotThrow(() -> participantService.calculCout());

        // Verify the log message for empty list was executed
        verify(eventRepository, times(1)).findAll();
        verify(eventRepository, never()).save(any(Evenement.class));

        log.info("✅ Empty list branch covered");
    }

    @Test
    void testCalculCout_NonEmptyEventsList_ShouldProcessAll() {
        log.info("=== Test: calculCout with non-empty list (ELSE BRANCH TEST) ===");

        // Create 3 events - tests the else branch
        Evenement e1 = new Evenement();
        e1.setId(1);
        e1.setDescription("Event 1");

        Evenement e2 = new Evenement();
        e2.setId(2);
        e2.setDescription("Event 2");

        Evenement e3 = new Evenement();
        e3.setId(3);
        e3.setDescription("Event 3");

        List<Evenement> events = new ArrayList<>();
        events.add(e1);
        events.add(e2);
        events.add(e3);

        // Mock repository behavior
        when(eventRepository.findAll()).thenReturn(events);
        when(logistiqueRepository.calculPrixLogistiquesReserves(true))
                .thenReturn(50.0f)
                .thenReturn(75.0f)
                .thenReturn(100.0f);
        when(eventRepository.save(any(Evenement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Execute
        participantService.calculCout();

        // Verify the ELSE branch (non-empty) was executed
        verify(eventRepository, times(1)).findAll();
        verify(logistiqueRepository, times(3)).calculPrixLogistiquesReserves(true);
        verify(eventRepository, times(3)).save(any(Evenement.class));

        // Verify cumulative cost calculation
        assertEquals(50.0f, e1.getCout(), 0.01);
        assertEquals(125.0f, e2.getCout(), 0.01);  // 50 + 75
        assertEquals(225.0f, e3.getCout(), 0.01);  // 50 + 75 + 100

        log.info("✅ Non-empty list branch covered");
    }

    @Test
    void testCalculCout_ForLoopExecution_AllIterations() {
        log.info("=== Test: calculCout FOR LOOP - all iterations ===");

        // Test the for(Evenement ev : evenements) loop
        List<Evenement> events = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Evenement e = new Evenement();
            e.setId(i);
            e.setDescription("Event " + i);
            e.setCout(0);
            events.add(e);
        }

        when(eventRepository.findAll()).thenReturn(events);
        when(logistiqueRepository.calculPrixLogistiquesReserves(true)).thenReturn(10.0f);
        when(eventRepository.save(any(Evenement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        participantService.calculCout();

        // Verify loop executed 5 times
        verify(logistiqueRepository, times(5)).calculPrixLogistiquesReserves(true);
        verify(eventRepository, times(5)).save(any(Evenement.class));

        log.info("✅ For loop branch covered - 5 iterations");
    }

    @Test
    void testGetParReservLogis_EmptyResult_ShouldReturnEmptyList() {
        log.info("=== Test: getParReservLogis with empty result ===");

        List<Participant> emptyList = new ArrayList<>();
        when(participantRepository.participReservLogis(true, Tache.ORGANISATEUR))
                .thenReturn(emptyList);

        List<Participant> result = participantService.getParReservLogis();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(participantRepository, times(1))
                .participReservLogis(true, Tache.ORGANISATEUR);

        log.info("✅ Empty result branch covered");
    }

    @Test
    void testGetParReservLogis_NonEmptyResult_ShouldReturnAll() {
        log.info("=== Test: getParReservLogis with multiple results ===");

        List<Participant> participants = new ArrayList<>();
        participants.add(new Participant(1, "Test1", "User1", Tache.ORGANISATEUR, null));
        participants.add(new Participant(2, "Test2", "User2", Tache.ORGANISATEUR, null));
        participants.add(new Participant(3, "Test3", "User3", Tache.ORGANISATEUR, null));

        when(participantRepository.participReservLogis(true, Tache.ORGANISATEUR))
                .thenReturn(participants);

        List<Participant> result = participantService.getParReservLogis();

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(participantRepository, times(1))
                .participReservLogis(true, Tache.ORGANISATEUR);

        log.info("✅ Non-empty result branch covered");
    }

    @Test
    void testAjouterParticipant_WithAllTacheEnumValues() {
        log.info("=== Test: ajouterParticipant with ALL Tache enum values ===");

        // Test ORGANISATEUR
        Participant org = new Participant();
        org.setNom("Org");
        org.setPrenom("Test");
        org.setTache(Tache.ORGANISATEUR);
        when(participantRepository.save(org)).thenReturn(org);
        participantService.ajouterParticipant(org);

        // Test INVITE
        Participant inv = new Participant();
        inv.setNom("Inv");
        inv.setPrenom("Test");
        inv.setTache(Tache.INVITE);
        when(participantRepository.save(inv)).thenReturn(inv);
        participantService.ajouterParticipant(inv);

        // Test SERVEUR
        Participant srv = new Participant();
        srv.setNom("Srv");
        srv.setPrenom("Test");
        srv.setTache(Tache.SERVEUR);
        when(participantRepository.save(srv)).thenReturn(srv);
        participantService.ajouterParticipant(srv);

        // Test ANIMATEUR
        Participant ani = new Participant();
        ani.setNom("Ani");
        ani.setPrenom("Test");
        ani.setTache(Tache.ANIMATEUR);
        when(participantRepository.save(ani)).thenReturn(ani);
        participantService.ajouterParticipant(ani);

        verify(participantRepository, times(4)).save(any(Participant.class));

        log.info("✅ All Tache enum values covered");
    }

    @Test
    void testAjouterParticipant_LoggingExecution() {
        log.info("=== Test: ajouterParticipant - verify all log statements execute ===");

        Participant p = new Participant();
        p.setIdPart(99);
        p.setNom("Logger");
        p.setPrenom("Test");
        p.setTache(Tache.ORGANISATEUR);

        when(participantRepository.save(p)).thenReturn(p);

        // Execute - this covers all log.info() calls in the method
        Participant result = participantService.ajouterParticipant(p);

        assertNotNull(result);
        assertEquals(99, result.getIdPart());
        verify(participantRepository, times(1)).save(p);

        log.info("✅ All logging branches covered");
    }

    @Test
    void testCalculCout_ZeroCostScenario() {
        log.info("=== Test: calculCout with zero cost from logistique ===");

        Evenement e = new Evenement();
        e.setId(1);
        e.setDescription("Free Event");
        e.setCout(0);

        List<Evenement> events = new ArrayList<>();
        events.add(e);

        when(eventRepository.findAll()).thenReturn(events);
        when(logistiqueRepository.calculPrixLogistiquesReserves(true)).thenReturn(0.0f);
        when(eventRepository.save(any(Evenement.class))).thenReturn(e);

        participantService.calculCout();

        assertEquals(0.0f, e.getCout(), 0.01);
        verify(eventRepository, times(1)).save(e);

        log.info("✅ Zero cost scenario covered");
    }

    @Test
    void testCalculCout_HighCostScenario() {
        log.info("=== Test: calculCout with high cost values ===");

        Evenement e = new Evenement();
        e.setId(1);
        e.setDescription("Luxury Event");
        e.setCout(0);

        List<Evenement> events = new ArrayList<>();
        events.add(e);

        when(eventRepository.findAll()).thenReturn(events);
        when(logistiqueRepository.calculPrixLogistiquesReserves(true)).thenReturn(9999.99f);
        when(eventRepository.save(any(Evenement.class))).thenReturn(e);

        participantService.calculCout();

        assertEquals(9999.99f, e.getCout(), 0.01);
        verify(eventRepository, times(1)).save(e);

        log.info("✅ High cost scenario covered");
    }
}