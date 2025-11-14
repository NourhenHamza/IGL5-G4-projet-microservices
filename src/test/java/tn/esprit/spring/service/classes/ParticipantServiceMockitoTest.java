package tn.esprit.spring.service.classes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Logistique;
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
        assertEquals("Ben Ali", result.getNom());
        verify(participantRepository, times(1)).save(p);
    }

    @Test
    void testAddMultipleParticipants() {
        Participant p1 = new Participant(1, "Doe", "John", Tache.ORGANISATEUR, null);
        Participant p2 = new Participant(2, "Smith", "Jane", Tache.INVITE, null);

        when(participantRepository.save(any(Participant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Participant result1 = participantService.ajouterParticipant(p1);
        Participant result2 = participantService.ajouterParticipant(p2);

        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("John", result1.getPrenom());
        assertEquals("Jane", result2.getPrenom());
        verify(participantRepository, times(2)).save(any(Participant.class));
    }

    @Test
    void testFindParticipant() {
        Participant p = new Participant(1, "Chakroun", "Eya", Tache.INVITE, null);
        when(participantRepository.findById(1)).thenReturn(Optional.of(p));

        Participant found = participantRepository.findById(1).orElse(null);

        assertNotNull(found);
        assertEquals("Eya", found.getPrenom());
        assertEquals("Chakroun", found.getNom());
        verify(participantRepository, times(1)).findById(1);
    }

    @Test
    void testFindParticipantNotFound() {
        when(participantRepository.findById(999)).thenReturn(Optional.empty());

        Participant found = participantRepository.findById(999).orElse(null);

        assertNull(found);
        verify(participantRepository, times(1)).findById(999);
    }

    @Test
    void testUpdateParticipant() {
        Participant updated = new Participant(1, "Sassi", "Mohamed", Tache.ORGANISATEUR, null);

        when(participantRepository.save(updated)).thenReturn(updated);

        Participant result = participantRepository.save(updated);

        assertEquals("Mohamed", result.getPrenom());
        assertEquals("Sassi", result.getNom());
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
    void testDeleteMultipleParticipants() {
        doNothing().when(participantRepository).deleteById(anyInt());

        participantRepository.deleteById(1);
        participantRepository.deleteById(2);
        participantRepository.deleteById(3);

        verify(participantRepository, times(3)).deleteById(anyInt());
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

    @Test
    void testCalculCout_WithNoEvents() {
        List<Evenement> emptyList = new ArrayList<>();
        when(eventRepository.findAll()).thenReturn(emptyList);

        assertDoesNotThrow(() -> participantService.calculCout());

        verify(eventRepository, times(1)).findAll();
        verify(eventRepository, never()).save(any(Evenement.class));
    }

    @Test
    void testCalculCout_WithSingleEvent() {
        Evenement event = new Evenement();
        event.setId(1);
        event.setDescription("Tech Conference");
        event.setCout(0);

        List<Evenement> events = new ArrayList<>();
        events.add(event);

        when(eventRepository.findAll()).thenReturn(events);
        when(logistiqueRepository.calculPrixLogistiquesReserves(true)).thenReturn(150.5f);
        when(eventRepository.save(any(Evenement.class))).thenReturn(event);

        participantService.calculCout();

        verify(eventRepository, times(1)).findAll();
        verify(logistiqueRepository, times(1)).calculPrixLogistiquesReserves(true);
        verify(eventRepository, times(1)).save(event);

        assertEquals(150.5f, event.getCout(), 0.01);
    }

    @Test
    void testCalculCout_WithMultipleEvents() {
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

        when(eventRepository.findAll()).thenReturn(events);
        when(logistiqueRepository.calculPrixLogistiquesReserves(true)).thenReturn(100.0f);
        when(eventRepository.save(any(Evenement.class))).thenAnswer(invocation -> invocation.getArgument(0));

        participantService.calculCout();

        verify(eventRepository, times(1)).findAll();
        verify(logistiqueRepository, times(2)).calculPrixLogistiquesReserves(true);
        verify(eventRepository, times(2)).save(any(Evenement.class));

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
    void testCalculCout_WithHighCosts() {
        Evenement event = new Evenement();
        event.setId(1);
        event.setDescription("Premium Event");
        event.setCout(0);

        List<Evenement> events = Arrays.asList(event);

        when(eventRepository.findAll()).thenReturn(events);
        when(logistiqueRepository.calculPrixLogistiquesReserves(true)).thenReturn(5000.0f);
        when(eventRepository.save(any(Evenement.class))).thenReturn(event);

        participantService.calculCout();

        assertEquals(5000.0f, event.getCout(), 0.01);
        verify(eventRepository, times(1)).save(event);
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

    @Test
    void testAddParticipant_WithNullValues() {
        Participant p = new Participant();
        p.setIdPart(3);
        p.setNom(null);
        p.setPrenom(null);
        p.setTache(Tache.ORGANISATEUR);

        when(participantRepository.save(p)).thenReturn(p);

        Participant result = participantService.ajouterParticipant(p);

        assertNotNull(result);
        assertNull(result.getNom());
        assertNull(result.getPrenom());
        assertEquals(Tache.ORGANISATEUR, result.getTache());
    }

    @Test
    void testFindAllParticipants() {
        List<Participant> mockList = Arrays.asList(
                new Participant(1, "Name1", "First1", Tache.ORGANISATEUR, null),
                new Participant(2, "Name2", "First2", Tache.INVITE, null),
                new Participant(3, "Name3", "First3", Tache.ORGANISATEUR, null)
        );

        when(participantRepository.findAll()).thenReturn(mockList);

        List<Participant> result = (List<Participant>) participantRepository.findAll();

        assertEquals(3, result.size());
        verify(participantRepository, times(1)).findAll();
    }

    @Test
    void testExistsById() {
        when(participantRepository.existsById(1)).thenReturn(true);
        when(participantRepository.existsById(999)).thenReturn(false);

        assertTrue(participantRepository.existsById(1));
        assertFalse(participantRepository.existsById(999));

        verify(participantRepository, times(1)).existsById(1);
        verify(participantRepository, times(1)).existsById(999);
    }

    @Test
    void testCountParticipants() {
        when(participantRepository.count()).thenReturn(5L);

        long count = participantRepository.count();

        assertEquals(5L, count);
        verify(participantRepository, times(1)).count();
    }

    @Test
    void testCalculCout_WithThreeEvents() {
        Evenement e1 = new Evenement();
        e1.setId(1);
        e1.setDescription("Event 1");
        e1.setCout(0);

        Evenement e2 = new Evenement();
        e2.setId(2);
        e2.setDescription("Event 2");
        e2.setCout(0);

        Evenement e3 = new Evenement();
        e3.setId(3);
        e3.setDescription("Event 3");
        e3.setCout(0);

        List<Evenement> events = Arrays.asList(e1, e2, e3);

        when(eventRepository.findAll()).thenReturn(events);
        when(logistiqueRepository.calculPrixLogistiquesReserves(true)).thenReturn(50.0f);
        when(eventRepository.save(any(Evenement.class))).thenAnswer(inv -> inv.getArgument(0));

        participantService.calculCout();

        assertEquals(50.0f, e1.getCout(), 0.01);
        assertEquals(100.0f, e2.getCout(), 0.01);
        assertEquals(150.0f, e3.getCout(), 0.01);
        verify(eventRepository, times(3)).save(any(Evenement.class));
    }

    @Test
    void testGetParReservLogis_VerifyCorrectParameters() {
        List<Participant> mockList = new ArrayList<>();
        when(participantRepository.participReservLogis(anyBoolean(), any(Tache.class)))
                .thenReturn(mockList);

        participantService.getParReservLogis();

        verify(participantRepository).participReservLogis(eq(true), eq(Tache.ORGANISATEUR));
    }
}