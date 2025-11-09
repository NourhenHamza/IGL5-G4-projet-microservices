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
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.TestPropertySource;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.persistence.entities.Tache;
import tn.esprit.spring.persistence.repositories.EvenementRepository;
import tn.esprit.spring.persistence.repositories.LogistiqueRepository;
import tn.esprit.spring.persistence.repositories.ParticipantRepository;


@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
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

    // ✅ CREATE
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

    // ✅ READ
    @Test
    void testFindParticipant() {
        Participant p = new Participant(1, "Chakroun", "Eya", Tache.INVITE, null);
        when(participantRepository.findById(1)).thenReturn(Optional.of(p));

        Participant found = participantRepository.findById(1).orElse(null);

        assertNotNull(found);
        assertEquals("Eya", found.getPrenom());
        verify(participantRepository, times(1)).findById(1);
    }

    // ✅ UPDATE
    @Test
    void testUpdateParticipant() {
        Participant existing = new Participant(1, "Sassi", "Khaled", Tache.ORGANISATEUR, null);
        Participant updated = new Participant(1, "Sassi", "Mohamed", Tache.ORGANISATEUR, null);

        when(participantRepository.save(updated)).thenReturn(updated);

        Participant result = participantRepository.save(updated);

        assertEquals("Mohamed", result.getPrenom());
        verify(participantRepository, times(1)).save(updated);
    }

    // ✅ DELETE
    @Test
    void testDeleteParticipant() {
        int id = 1;
        doNothing().when(participantRepository).deleteById(id);

        participantRepository.deleteById(id);

        verify(participantRepository, times(1)).deleteById(id);
    }

    // ✅ EXTRA: Test getParReservLogis()
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
}
