package tn.esprit.spring.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.service.classes.SalleService;
import tn.esprit.spring.persistence.entities.Salle;
import tn.esprit.spring.persistence.repositories.SalleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SalleServiceTest {

    @Mock
    private SalleRepository salleRepository;

    @InjectMocks
    private SalleService salleService;

    @Test
    public void testAjouterSalle() {
        System.out.println("[TEST] Test d'ajout d'une salle");

        Salle salle = new Salle();
        salle.setNom("Salle A");
        salle.setAdresse("123 Avenue Habib Bourguiba");
        salle.setCapacite(50);
        salle.setDisponible(true);

        when(salleRepository.save(salle)).thenReturn(salle);

        Salle result = salleService.ajouterSalle(salle);

        assertNotNull(result);
        assertEquals("Salle A", result.getNom());
        assertEquals(50, result.getCapacite());
        assertTrue(result.isDisponible());
        verify(salleRepository, times(1)).save(salle);
        System.out.println("[TEST] Test d'ajout réussi ✓");
    }

    @Test
    public void testModifierSalle() {
        System.out.println("[TEST] Test de modification d'une salle");

        Salle salle = new Salle();
        salle.setIdSalle(1);
        salle.setNom("Salle B");
        salle.setAdresse("456 Rue de la Liberté");
        salle.setCapacite(100);

        when(salleRepository.save(salle)).thenReturn(salle);

        Salle result = salleService.modifierSalle(salle);

        assertNotNull(result);
        assertEquals(1, result.getIdSalle());
        assertEquals("Salle B", result.getNom());
        verify(salleRepository, times(1)).save(salle);
        System.out.println("[TEST] Test de modification réussi ✓");
    }

    @Test
    public void testSupprimerSalle() {
        System.out.println("[TEST] Test de suppression d'une salle");

        int id = 1;

        salleService.supprimerSalle(id);

        verify(salleRepository, times(1)).deleteById(id);
        System.out.println("[TEST] Test de suppression réussi ✓");
    }

    @Test
    public void testGetAllSalles() {
        System.out.println("[TEST] Test de récupération de toutes les salles");

        List<Salle> salles = new ArrayList<>();
        Salle salle1 = new Salle();
        salle1.setNom("Salle A");
        salle1.setCapacite(50);

        Salle salle2 = new Salle();
        salle2.setNom("Salle B");
        salle2.setCapacite(100);

        salles.add(salle1);
        salles.add(salle2);

        when(salleRepository.findAll()).thenReturn(salles);



        List<Salle> result = salleService.getAllSalles();



        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Salle A", result.get(0).getNom());
        assertEquals("Salle B", result.get(1).getNom());
        verify(salleRepository, times(1)).findAll();
        System.out.println("[TEST] Test de récupération réussi - " + result.size() + " salles trouvées ✓");
    }

    @Test
    public void testGetSalleById() {
        System.out.println("[TEST] Test de récupération d'une salle par ID");

        int id = 1;
        Salle salle = new Salle();
        salle.setIdSalle(id);
        salle.setNom("Salle A");
        salle.setCapacite(50);

        when(salleRepository.findById(id)).thenReturn(Optional.of(salle));

        Optional<Salle> result = salleService.getSalleById(id);

        assertTrue(result.isPresent());
        assertEquals("Salle A", result.get().getNom());
        assertEquals(50, result.get().getCapacite());
        verify(salleRepository, times(1)).findById(id);
        System.out.println("[TEST] Test de récupération par ID réussi ✓");
    }

    @Test
    public void testGetSallesDisponibles() {
        System.out.println("[TEST] Test de récupération des salles disponibles");

        List<Salle> sallesDisponibles = new ArrayList<>();
        Salle salle1 = new Salle();
        salle1.setNom("Salle A");
        salle1.setDisponible(true);
        salle1.setCapacite(50);
        sallesDisponibles.add(salle1);

        when(salleRepository.findByDisponible(true)).thenReturn(sallesDisponibles);

        List<Salle> result = salleService.getSallesDisponibles();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isDisponible());
        verify(salleRepository, times(1)).findByDisponible(true);
        System.out.println("[TEST] Test des salles disponibles réussi - " + result.size() + " salle(s) disponible(s) ✓");
    }

    @Test
    public void testGetSallesParCapaciteMin() {
        System.out.println("[TEST] Test de récupération des salles par capacité minimale");

        int capaciteMin = 50;
        List<Salle> salles = new ArrayList<>();
        Salle salle1 = new Salle();
        salle1.setNom("Grande Salle");
        salle1.setCapacite(100);
        salles.add(salle1);

        when(salleRepository.findByCapaciteGreaterThanEqual(capaciteMin)).thenReturn(salles);

        List<Salle> result = salleService.getSallesParCapaciteMin(capaciteMin);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getCapacite() >= capaciteMin);
        verify(salleRepository, times(1)).findByCapaciteGreaterThanEqual(capaciteMin);
        System.out.println("[TEST] Test de capacité minimale réussi - " + result.size() + " salle(s) trouvée(s) ✓");
    }

    @Test
    public void testReserverSalle() {
        System.out.println("[TEST] Test de réservation d'une salle");

        int id = 1;
        Salle salle = new Salle();
        salle.setIdSalle(id);
        salle.setNom("Salle A");
        salle.setDisponible(true);

        when(salleRepository.findById(id)).thenReturn(Optional.of(salle));
        when(salleRepository.save(salle)).thenReturn(salle);

        Salle result = salleService.reserverSalle(id);

        assertNotNull(result);
        assertFalse(result.isDisponible());
        verify(salleRepository, times(1)).findById(id);
        verify(salleRepository, times(1)).save(salle);
        System.out.println("[TEST] Test de réservation réussi - Salle marquée comme non disponible ✓");
    }

    @Test
    public void testReserverSalleNonDisponible() {
        System.out.println("[TEST] Test de réservation d'une salle déjà réservée");

        int id = 1;
        Salle salle = new Salle();
        salle.setIdSalle(id);
        salle.setNom("Salle A");
        salle.setDisponible(false);

        when(salleRepository.findById(id)).thenReturn(Optional.of(salle));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            salleService.reserverSalle(id);
        });

        assertEquals("La salle n'est pas disponible", exception.getMessage());
        verify(salleRepository, times(1)).findById(id);
        verify(salleRepository, times(0)).save(any());
        System.out.println("[TEST] Test de salle non disponible réussi - Exception levée correctement ✓");
    }

    @Test
    public void testLibererSalle() {
        System.out.println("[TEST] Test de libération d'une salle");

        int id = 1;
        Salle salle = new Salle();
        salle.setIdSalle(id);
        salle.setNom("Salle A");
        salle.setDisponible(false);

        when(salleRepository.findById(id)).thenReturn(Optional.of(salle));
        when(salleRepository.save(salle)).thenReturn(salle);

        Salle result = salleService.libererSalle(id);

        assertNotNull(result);
        assertTrue(result.isDisponible());
        verify(salleRepository, times(1)).findById(id);
        verify(salleRepository, times(1)).save(salle);
        System.out.println("[TEST] Test de libération réussi - Salle marquée comme disponible ✓");
    }

    @Test
    public void testReserverSalleIntrouvable() {
        System.out.println("[TEST] Test de réservation d'une salle introuvable");

        int id = 999;
        when(salleRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            salleService.reserverSalle(id);
        });

        assertEquals("Salle introuvable", exception.getMessage());
        verify(salleRepository, times(1)).findById(id);
        System.out.println("[TEST] Test de salle introuvable réussi - Exception levée correctement ✓");
    }

    @Test
    public void testLibererSalleIntrouvable() {
        System.out.println("[TEST] Test de libération d'une salle introuvable");

        int id = 999;
        when(salleRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            salleService.libererSalle(id);
        });

        assertEquals("Salle introuvable", exception.getMessage());
        verify(salleRepository, times(1)).findById(id);
        System.out.println("[TEST] Test de libération de salle introuvable réussi - Exception levée correctement ✓");
    }
}