package tn.esprit.spring.service.classes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.persistence.entities.Tache;
import tn.esprit.spring.persistence.repositories.EvenementRepository;
import tn.esprit.spring.persistence.repositories.LogistiqueRepository;
import tn.esprit.spring.persistence.repositories.ParticipantRepository;

public class ParticipantServiceMockitoTest {

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private EvenementRepository eventRepository;

    @Mock
    private LogistiqueRepository logistiqueRepository;

    @InjectMocks
    private ParticipantServiceImpl participantService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddParticipant() {
        Participant p = new Participant();
        p.setIdPart(1);
        p.setNom("Ben Ali");
        p.setPrenom("Yasmine");
        p.setTache(Tache.ORGANISATEUR);

        when(participantRepository.save(p)).thenReturn(p);

        Participant result = participantService.ajouterParticipant(p);

        assertNotNull(result);
        assertEquals("Yasmine", result.getPrenom());
        verify(participantRepository, times(1)).save(p);
    }

    @Test
    void testFindParticipant() {
        Participant p = new Participant(1, "Chakroun", "Eya", Tache.INVITE, null);
        when(participantRepository.findById(1)).thenReturn(Optional.of(p));

        Participant found = participantRepository.findById(1).orElse(null);

        assertNotNull(found);
        assertEquals("Eya", found.getPrenom());
        verify(participantRepository, times(1)).findById(1);
    }

    @Test
    void testUpdateParticipant() {
        Participant updated = new Participant(1, "Sassi", "Mohamed", Tache.ORGANISATEUR, null);

        when(participantRepository.save(updated)).thenReturn(updated);

        Participant result = participantRepository.save(updated);

        assertEquals("Mohamed", result.getPrenom());
        verify(participantRepository, times(1)).save(updated);
    }

    @Test
    void testDeleteParticipant() {
        int id = 1;
        doNothing().when(participantRepository).deleteById(id);

        participantRepository.deleteById(id);

        verify(participantRepository, times(1)).deleteById(id);
    }

    @Test
    void testGetParReservLogis() {
        List<Participant> mockList = new ArrayList<>();
        mockList.add(new Participant(1, "Ammar", "Lina", Tache.ORGANISATEUR, null));

        when(participantRepository.participReservLogis(true, Tache.ORGANISATEUR)).thenReturn(mockList);

        List<Participant> result = participantService.getParReservLogis();

        assertEquals(1, result.size());
        assertEquals("Lina", result.get(0).getPrenom());
        verify(participantRepository, times(1)).participReservLogis(true, Tache.ORGANISATEUR);
    }

    // ========================================
    // 🆕 NEW TESTS FOR MISSING COVERAGE
    // ========================================

    @Test
    void testCalculCout_WithNoEvents() {
        // Mock empty event list
        List<Evenement> emptyList = new ArrayList<>();
        when(eventRepository.findAll()).thenReturn(emptyList);

        // Should complete without errors
        assertDoesNotThrow(() -> participantService.calculCout());

        // Verify findAll was called
        verify(eventRepository, times(1)).findAll();
        // Verify save was never called (no events to update)
        verify(eventRepository, never()).save(any(Evenement.class));
    }

    @Test
    void testCalculCout_WithSingleEvent() {
        // Create mock event
        Evenement event = new Evenement();
        event.setId(1);
        event.setDescription("Tech Conference");
        event.setCout(0);

        List<Evenement> events = new ArrayList<>();
        events.add(event);

        // Mock repository responses
        when(eventRepository.findAll()).thenReturn(events);
        when(logistiqueRepository.calculPrixLogistiquesReserves(true)).thenReturn(150.5f);
        when(eventRepository.save(any(Evenement.class))).thenReturn(event);

        // Execute method
        participantService.calculCout();

        // Verify interactions
        verify(eventRepository, times(1)).findAll();
        verify(logistiqueRepository, times(1)).calculPrixLogistiquesReserves(true);
        verify(eventRepository, times(1)).save(event);

        // Verify cost was set
        assertEquals(150.5f, event.getCout(), 0.01);
    }

    @Test
    void testCalculCout_WithMultipleEvents() {
        // Create mock events
        Evenement event1 = new Evenement();
        event1.setId(1);
        event1.setDescription("Conference 1");
        event1.setCout(0);

        Evenement event2 = new Evenement();
        event2.setId(2);
        event2.setDescription("Conference 2");
        event2.setCout(0);

        List<Evenement> events = new ArrayList<>();
        events.add(event1);
        events.add(event2);

        // Mock repository responses
        when(eventRepository.findAll()).thenReturn(events);
        when(logistiqueRepository.calculPrixLogistiquesReserves(true)).thenReturn(100.0f);
        when(eventRepository.save(any(Evenement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Execute method
        participantService.calculCout();

        // Verify interactions
        verify(eventRepository, times(1)).findAll();
        verify(logistiqueRepository, times(2)).calculPrixLogistiquesReserves(true);
        verify(eventRepository, times(2)).save(any(Evenement.class));

        // Verify cumulative costs (100 for first, 200 for second)
        assertEquals(100.0f, event1.getCout(), 0.01);
        assertEquals(200.0f, event2.getCout(), 0.01);
    }

    @Test
    void testCalculCout_WithZeroCost() {
        Evenement event = new Evenement();
        event.setId(1);
        event.setDescription("Free Event");
        event.setCout(0);

        List<Evenement> events = new ArrayList<>();
        events.add(event);

        when(eventRepository.findAll()).thenReturn(events);
        when(logistiqueRepository.calculPrixLogistiquesReserves(true)).thenReturn(0.0f);
        when(eventRepository.save(any(Evenement.class))).thenReturn(event);

        participantService.calculCout();

        verify(eventRepository, times(1)).save(event);
        assertEquals(0.0f, event.getCout(), 0.01);
    }

    @Test
    void testGetParReservLogis_WithEmptyList() {
        List<Participant> emptyList = new ArrayList<>();
        when(participantRepository.participReservLogis(true, Tache.ORGANISATEUR)).thenReturn(emptyList);

        List<Participant> result = participantService.getParReservLogis();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(participantRepository, times(1)).participReservLogis(true, Tache.ORGANISATEUR);
    }

    @Test
    void testGetParReservLogis_WithMultipleOrganisateurs() {
        List<Participant> mockList = new ArrayList<>();
        mockList.add(new Participant(1, "Trabelsi", "Ahmed", Tache.ORGANISATEUR, null));
        mockList.add(new Participant(2, "Jemli", "Salma", Tache.ORGANISATEUR, null));
        mockList.add(new Participant(3, "Karoui", "Nour", Tache.ORGANISATEUR, null));

        when(participantRepository.participReservLogis(true, Tache.ORGANISATEUR)).thenReturn(mockList);

        List<Participant> result = participantService.getParReservLogis();

        assertEquals(3, result.size());
        assertEquals("Ahmed", result.get(0).getPrenom());
        assertEquals("Salma", result.get(1).getPrenom());
        assertEquals("Nour", result.get(2).getPrenom());
        verify(participantRepository, times(1)).participReservLogis(true, Tache.ORGANISATEUR);
    }

    @Test
    void testAddParticipant_WithInviteTache() {
        Participant p = new Participant();
        p.setIdPart(2);
        p.setNom("Zouari");
        p.setPrenom("Leila");
        p.setTache(Tache.INVITE);

        when(participantRepository.save(p)).thenReturn(p);

        Participant result = participantService.ajouterParticipant(p);

        assertNotNull(result);
        assertEquals("Leila", result.getPrenom());
        assertEquals(Tache.INVITE, result.getTache());
        verify(participantRepository, times(1)).save(p);
    }
}