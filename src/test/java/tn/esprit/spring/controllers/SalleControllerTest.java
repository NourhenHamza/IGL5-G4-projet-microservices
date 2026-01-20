package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.spring.dto.SalleDTO;
import tn.esprit.spring.persistence.entities.Salle;
import tn.esprit.spring.service.classes.SalleService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SalleControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SalleService salleService;

    @InjectMocks
    private SalleController salleController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(salleController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAjouterSalle() throws Exception {
        System.out.println("[CONTROLLER TEST] Test d'ajout d'une salle via API");

        SalleDTO salleDTO = new SalleDTO(null, "Salle A", "123 Avenue Habib Bourguiba", 50, true);
        
        Salle savedSalle = new Salle();
        savedSalle.setIdSalle(1);
        savedSalle.setNom("Salle A");
        savedSalle.setAdresse("123 Avenue Habib Bourguiba");
        savedSalle.setCapacite(50);
        savedSalle.setDisponible(true);

        when(salleService.ajouterSalle(any(Salle.class))).thenReturn(savedSalle);

        mockMvc.perform(post("/salles/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(salleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSalle").value(1))
                .andExpect(jsonPath("$.nom").value("Salle A"))
                .andExpect(jsonPath("$.capacite").value(50))
                .andExpect(jsonPath("$.disponible").value(true));

        verify(salleService, times(1)).ajouterSalle(any(Salle.class));
        System.out.println("[CONTROLLER TEST] Test d'ajout réussi ✓");
    }

    @Test
    public void testModifierSalle() throws Exception {
        System.out.println("[CONTROLLER TEST] Test de modification d'une salle via API");

        SalleDTO salleDTO = new SalleDTO(1, "Salle B Updated", "456 Rue de la Liberté", 100, false);
        
        Salle updatedSalle = new Salle();
        updatedSalle.setIdSalle(1);
        updatedSalle.setNom("Salle B Updated");
        updatedSalle.setAdresse("456 Rue de la Liberté");
        updatedSalle.setCapacite(100);
        updatedSalle.setDisponible(false);

        when(salleService.modifierSalle(any(Salle.class))).thenReturn(updatedSalle);

        mockMvc.perform(put("/salles/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(salleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSalle").value(1))
                .andExpect(jsonPath("$.nom").value("Salle B Updated"))
                .andExpect(jsonPath("$.capacite").value(100));

        verify(salleService, times(1)).modifierSalle(any(Salle.class));
        System.out.println("[CONTROLLER TEST] Test de modification réussi ✓");
    }

    @Test
    public void testSupprimerSalle() throws Exception {
        System.out.println("[CONTROLLER TEST] Test de suppression d'une salle via API");

        doNothing().when(salleService).supprimerSalle(anyInt());

        mockMvc.perform(delete("/salles/delete/1"))
                .andExpect(status().isNoContent());

        verify(salleService, times(1)).supprimerSalle(1);
        System.out.println("[CONTROLLER TEST] Test de suppression réussi ✓");
    }

    @Test
    public void testGetAllSalles() throws Exception {
        System.out.println("[CONTROLLER TEST] Test de récupération de toutes les salles via API");

        Salle salle1 = new Salle();
        salle1.setIdSalle(1);
        salle1.setNom("Salle A");
        salle1.setCapacite(50);
        salle1.setDisponible(true);

        Salle salle2 = new Salle();
        salle2.setIdSalle(2);
        salle2.setNom("Salle B");
        salle2.setCapacite(100);
        salle2.setDisponible(false);

        List<Salle> salles = Arrays.asList(salle1, salle2);
        when(salleService.getAllSalles()).thenReturn(salles);

        mockMvc.perform(get("/salles/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Salle A"))
                .andExpect(jsonPath("$[1].nom").value("Salle B"))
                .andExpect(jsonPath("$.length()").value(2));

        verify(salleService, times(1)).getAllSalles();
        System.out.println("[CONTROLLER TEST] Test de récupération de toutes les salles réussi ✓");
    }

    @Test
    public void testGetSalleById() throws Exception {
        System.out.println("[CONTROLLER TEST] Test de récupération d'une salle par ID via API");

        Salle salle = new Salle();
        salle.setIdSalle(1);
        salle.setNom("Salle A");
        salle.setCapacite(50);
        salle.setDisponible(true);

        when(salleService.getSalleById(1)).thenReturn(Optional.of(salle));

        mockMvc.perform(get("/salles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSalle").value(1))
                .andExpect(jsonPath("$.nom").value("Salle A"));

        verify(salleService, times(1)).getSalleById(1);
        System.out.println("[CONTROLLER TEST] Test de récupération par ID réussi ✓");
    }

    @Test
    public void testGetSalleByIdNotFound() throws Exception {
        System.out.println("[CONTROLLER TEST] Test de récupération d'une salle inexistante via API");

        when(salleService.getSalleById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/salles/999"))
                .andExpect(status().isNotFound());

        verify(salleService, times(1)).getSalleById(999);
        System.out.println("[CONTROLLER TEST] Test de salle inexistante réussi ✓");
    }

    @Test
    public void testGetSallesDisponibles() throws Exception {
        System.out.println("[CONTROLLER TEST] Test de récupération des salles disponibles via API");

        Salle salle = new Salle();
        salle.setIdSalle(1);
        salle.setNom("Salle A");
        salle.setDisponible(true);

        List<Salle> salles = Arrays.asList(salle);
        when(salleService.getSallesDisponibles()).thenReturn(salles);

        mockMvc.perform(get("/salles/disponibles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].disponible").value(true))
                .andExpect(jsonPath("$.length()").value(1));

        verify(salleService, times(1)).getSallesDisponibles();
        System.out.println("[CONTROLLER TEST] Test des salles disponibles réussi ✓");
    }

    @Test
    public void testGetSallesParCapacite() throws Exception {
        System.out.println("[CONTROLLER TEST] Test de récupération des salles par capacité via API");

        Salle salle = new Salle();
        salle.setIdSalle(1);
        salle.setNom("Grande Salle");
        salle.setCapacite(100);

        List<Salle> salles = Arrays.asList(salle);
        when(salleService.getSallesParCapaciteMin(50)).thenReturn(salles);

        mockMvc.perform(get("/salles/capacite/50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].capacite").value(100))
                .andExpect(jsonPath("$.length()").value(1));

        verify(salleService, times(1)).getSallesParCapaciteMin(50);
        System.out.println("[CONTROLLER TEST] Test de capacité minimale réussi ✓");
    }

    @Test
    public void testReserverSalle() throws Exception {
        System.out.println("[CONTROLLER TEST] Test de réservation d'une salle via API");

        Salle salle = new Salle();
        salle.setIdSalle(1);
        salle.setNom("Salle A");
        salle.setDisponible(false);

        when(salleService.reserverSalle(1)).thenReturn(salle);

        mockMvc.perform(put("/salles/reserver/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(false));

        verify(salleService, times(1)).reserverSalle(1);
        System.out.println("[CONTROLLER TEST] Test de réservation réussi ✓");
    }

    @Test
    public void testLibererSalle() throws Exception {
        System.out.println("[CONTROLLER TEST] Test de libération d'une salle via API");

        Salle salle = new Salle();
        salle.setIdSalle(1);
        salle.setNom("Salle A");
        salle.setDisponible(true);

        when(salleService.libererSalle(1)).thenReturn(salle);

        mockMvc.perform(put("/salles/liberer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(true));

        verify(salleService, times(1)).libererSalle(1);
        System.out.println("[CONTROLLER TEST] Test de libération réussi ✓");
    }
}