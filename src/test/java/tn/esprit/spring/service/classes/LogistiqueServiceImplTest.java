package tn.esprit.spring.service.classes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Logistique;
import tn.esprit.spring.persistence.repositories.EvenementRepository;
import tn.esprit.spring.persistence.repositories.LogistiqueRepository;
import tn.esprit.spring.service.interfaces.ILogistiqueService;

// ============================================
// TESTS SANS MOCKITO (avec vraie base de données)
// ============================================
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@Transactional
public class LogistiqueServiceImplTest {

    @Autowired
    ILogistiqueService logistiqueService;

    @Autowired
    LogistiqueRepository logistiqueRepository;

    @Autowired
    EvenementRepository evenementRepository;

    /**
     * Test 1 : Ajouter une logistique et l'affecter à un événement
     * @Transactional fait automatiquement un rollback - pas besoin de nettoyage manuel
     */
    @Test
    public void testAddLogistique() throws ParseException {
        log.info("=== Début du test d'ajout de logistique ===");
        
        // Créer un événement avec un nom unique
        String uniqueEventName = "Festival_Add_" + UUID.randomUUID().toString();
        Evenement event = new Evenement();
        event.setDescription(uniqueEventName);
        event.setDated(new Date());
        event.setDatef(new Date());
        event.setCout(1000.0f);
        evenementRepository.save(event);
        
        // Créer une logistique
        Logistique logistique = new Logistique();
        logistique.setDescription("Tente de camping");
        logistique.setReserve(true);
        logistique.setPrix(150.5f);
        logistique.setQuantite(10);
        
        // Sauvegarder et affecter
        Logistique savedLogistique = logistiqueService.ajoutAffectLogEven(logistique, uniqueEventName);
        
        // Vérifications
        assertNotNull(savedLogistique);
        assertTrue(savedLogistique.getIdlog() > 0);
        assertEquals("Tente de camping", savedLogistique.getDescription());
        assertTrue(savedLogistique.getPrix() > 0);
        assertEquals(10, savedLogistique.getQuantite());
        
        log.info("Logistique ajoutée avec succès: ID={}, Description={}", 
                 savedLogistique.getIdlog(), savedLogistique.getDescription());
        
        log.info("=== Fin du test d'ajout de logistique (rollback automatique) ===");
    }

    /**
     * Test 2 : Récupérer une logistique par ID
     */
    @Test
    public void testGetLogistiqueById() throws ParseException {
        log.info("=== Début du test de récupération de logistique par ID ===");
        
        // Créer un événement avec un nom unique
        String uniqueEventName = "Festival_Get_" + UUID.randomUUID().toString();
        Evenement event = new Evenement();
        event.setDescription(uniqueEventName);
        event.setDated(new Date());
        event.setDatef(new Date());
        event.setCout(500.0f);
        evenementRepository.save(event);
        
        // Créer une logistique
        Logistique logistique = new Logistique();
        logistique.setDescription("Chaise pliante");
        logistique.setReserve(false);
        logistique.setPrix(25.0f);
        logistique.setQuantite(50);
        
        Logistique savedLogistique = logistiqueService.ajoutAffectLogEven(logistique, uniqueEventName);
        int logistiqueId = savedLogistique.getIdlog();
        
        // Récupérer par ID
        Optional<Logistique> foundLogistique = logistiqueRepository.findById(logistiqueId);
        
        // Vérifications
        assertTrue(foundLogistique.isPresent());
        assertEquals("Chaise pliante", foundLogistique.get().getDescription());
        assertEquals(25.0f, foundLogistique.get().getPrix(), 0.001);
        assertEquals(50, foundLogistique.get().getQuantite());
        
        log.info("Logistique récupérée avec ID: {}", logistiqueId);
        log.info("=== Fin du test de récupération (rollback automatique) ===");
    }

    /**
     * Test 3 : Mettre à jour une logistique
     */
    @Test
    public void testUpdateLogistique() throws ParseException {
        log.info("=== Début du test de mise à jour de logistique ===");
        
        // Créer un événement avec un nom unique
        String uniqueEventName = "Festival_Update_" + UUID.randomUUID().toString();
        Evenement event = new Evenement();
        event.setDescription(uniqueEventName);
        event.setDated(new Date());
        event.setDatef(new Date());
        event.setCout(800.0f);
        evenementRepository.save(event);
        
        // Création initiale
        Logistique logistique = new Logistique();
        logistique.setDescription("Table basse");
        logistique.setReserve(true);
        logistique.setPrix(75.0f);
        logistique.setQuantite(20);
        
        Logistique savedLogistique = logistiqueService.ajoutAffectLogEven(logistique, uniqueEventName);
        
        // Modification
        savedLogistique.setPrix(85.0f);
        savedLogistique.setQuantite(25);
        Logistique updatedLogistique = logistiqueRepository.save(savedLogistique);
        
        // Vérifications
        assertEquals(85.0f, updatedLogistique.getPrix(), 0.001);
        assertEquals(25, updatedLogistique.getQuantite());
        assertEquals("Table basse", updatedLogistique.getDescription());
        
        log.info("Logistique mise à jour - Nouveau prix: {}, Nouvelle quantité: {}", 
                 updatedLogistique.getPrix(), updatedLogistique.getQuantite());
        
        log.info("=== Fin du test de mise à jour (rollback automatique) ===");
    }

    /**
     * Test 4 : Supprimer une logistique
     */
    @Test
    public void testDeleteLogistique() throws ParseException {
        log.info("=== Début du test de suppression de logistique ===");
        
        // Créer un événement avec un nom unique
        String uniqueEventName = "Festival_Delete_" + UUID.randomUUID().toString();
        Evenement event = new Evenement();
        event.setDescription(uniqueEventName);
        event.setDated(new Date());
        event.setDatef(new Date());
        event.setCout(600.0f);
        evenementRepository.save(event);
        
        // Créer une logistique à supprimer
        Logistique logistique = new Logistique();
        logistique.setDescription("Éclairage LED");
        logistique.setReserve(true);
        logistique.setPrix(200.0f);
        logistique.setQuantite(5);
        
        Logistique savedLogistique = logistiqueService.ajoutAffectLogEven(logistique, uniqueEventName);
        int logistiqueId = savedLogistique.getIdlog();
        
        // Vérifier que la logistique existe
        assertTrue(logistiqueRepository.findById(logistiqueId).isPresent());
        
        // Supprimer
        logistiqueRepository.deleteById(logistiqueId);
        
        // Vérifier la suppression
        assertFalse(logistiqueRepository.findById(logistiqueId).isPresent());
        
        log.info("Logistique supprimée avec succès - ID: {}", logistiqueId);
        log.info("=== Fin du test de suppression (rollback automatique) ===");
    }

    /**
     * Test bonus : Récupérer les logistiques par dates
     */
    @Test
    public void testGetLogistiquesDates() throws ParseException {
        log.info("=== Début du test de récupération des logistiques par dates ===");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateDebut = dateFormat.parse("01/01/2023");
        Date dateFin = dateFormat.parse("31/12/2023");
        
        List<Logistique> logistiques = logistiqueService.getLogistiquesDates(dateDebut, dateFin);
        
        assertNotNull(logistiques);
        log.info("Nombre de logistiques trouvées: {}", logistiques.size());
        
        log.info("=== Fin du test de récupération par dates ===");
    }
}

// ============================================
// TESTS AVEC MOCKITO (sans base de données)
// ============================================
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
class LogistiqueServiceImplMockTest {

    @Autowired
    private ILogistiqueService logistiqueService;

    @MockBean
    private LogistiqueRepository logistiqueRepository;

    @MockBean
    private EvenementRepository evenementRepository;

    /**
     * Test 1 Mockito : Ajouter une logistique
     */
    @Test
    public void testAddLogistiqueWithMock() throws ParseException {
        log.info("=== Début du test Mockito d'ajout de logistique ===");
        
        // Arrange - Préparer les données
        Logistique logistique = new Logistique();
        logistique.setIdlog(1);
        logistique.setDescription("Tente de camping");
        logistique.setReserve(true);
        logistique.setPrix(150.5f);
        logistique.setQuantite(10);
        
        Evenement event = new Evenement();
        event.setId(1);
        event.setDescription("Festival Test Mock");
        event.setLogistiques(new ArrayList<>());
        
        // Configurer les mocks
        when(evenementRepository.findByDescription("Festival Test Mock")).thenReturn(event);
        when(logistiqueRepository.save(any(Logistique.class))).thenReturn(logistique);
        when(evenementRepository.save(any(Evenement.class))).thenReturn(event);
        
        // Act - Exécuter la méthode à tester
        Logistique result = logistiqueService.ajoutAffectLogEven(logistique, "Festival Test Mock");
        
        // Assert - Vérifier les résultats
        assertNotNull(result);
        assertEquals("Tente de camping", result.getDescription());
        assertEquals(150.5f, result.getPrix(), 0.001);
        verify(logistiqueRepository, times(1)).save(any(Logistique.class));
        verify(evenementRepository, times(1)).save(any(Evenement.class));
        
        log.info("Test Mockito réussi - Logistique ajoutée: {}", result.getDescription());
        log.info("=== Fin du test Mockito d'ajout ===");
    }

    /**
     * Test 2 Mockito : Récupérer une logistique par ID
     */
    @Test
    public void testGetLogistiqueByIdWithMock() {
        log.info("=== Début du test Mockito de récupération par ID ===");
        
        // Arrange
        Logistique logistique = new Logistique();
        logistique.setIdlog(1);
        logistique.setDescription("Chaise pliante");
        logistique.setPrix(25.0f);
        logistique.setQuantite(50);
        
        when(logistiqueRepository.findById(1)).thenReturn(Optional.of(logistique));
        
        // Act
        Optional<Logistique> found = logistiqueRepository.findById(1);
        
        // Assert
        assertTrue(found.isPresent());
        assertEquals("Chaise pliante", found.get().getDescription());
        assertEquals(25.0f, found.get().getPrix(), 0.001);
        verify(logistiqueRepository, times(1)).findById(1);
        
        log.info("Test Mockito réussi - Logistique trouvée: {}", found.get().getDescription());
        log.info("=== Fin du test Mockito de récupération ===");
    }

    /**
     * Test 3 Mockito : Mettre à jour une logistique
     */
    @Test
    public void testUpdateLogistiqueWithMock() {
        log.info("=== Début du test Mockito de mise à jour ===");
        
        // Arrange
        Logistique logistique = new Logistique();
        logistique.setIdlog(1);
        logistique.setDescription("Table basse");
        logistique.setPrix(75.0f);
        logistique.setQuantite(20);
        
        Logistique updatedLogistique = new Logistique();
        updatedLogistique.setIdlog(1);
        updatedLogistique.setDescription("Table basse");
        updatedLogistique.setPrix(85.0f);
        updatedLogistique.setQuantite(25);
        
        when(logistiqueRepository.save(any(Logistique.class))).thenReturn(updatedLogistique);
        
        // Act
        Logistique result = logistiqueRepository.save(logistique);
        
        // Assert
        assertNotNull(result);
        assertEquals(85.0f, result.getPrix(), 0.001);
        assertEquals(25, result.getQuantite());
        verify(logistiqueRepository, times(1)).save(any(Logistique.class));
        
        log.info("Test Mockito réussi - Prix mis à jour: {}", result.getPrix());
        log.info("=== Fin du test Mockito de mise à jour ===");
    }

    /**
     * Test 4 Mockito : Supprimer une logistique
     */
    @Test
    public void testDeleteLogistiqueWithMock() {
        log.info("=== Début du test Mockito de suppression ===");
        
        // Arrange
        int logistiqueId = 1;
        doNothing().when(logistiqueRepository).deleteById(logistiqueId);
        
        // Act
        logistiqueRepository.deleteById(logistiqueId);
        
        // Assert
        verify(logistiqueRepository, times(1)).deleteById(logistiqueId);
        
        log.info("Test Mockito réussi - Logistique supprimée avec ID: {}", logistiqueId);
        log.info("=== Fin du test Mockito de suppression ===");
    }

    /**
     * Test bonus Mockito : Récupérer les logistiques par dates
     */
    @Test
    public void testGetLogistiquesDatesWithMock() throws ParseException {
        log.info("=== Début du test Mockito de récupération par dates ===");
        
        // Arrange
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dateDebut = dateFormat.parse("01/01/2023");
        Date dateFin = dateFormat.parse("31/12/2023");
        
        List<Evenement> events = new ArrayList<>();
        Evenement event = new Evenement();
        event.setId(1);
        event.setDescription("Festival Test");
        event.setDated(dateFormat.parse("15/06/2023"));
        
        Logistique logistique = new Logistique();
        logistique.setIdlog(1);
        logistique.setDescription("Tente");
        logistique.setReserve(true);
        logistique.setPrix(100.0f);
        logistique.setQuantite(5);
        
        List<Logistique> logistiques = new ArrayList<>();
        logistiques.add(logistique);
        event.setLogistiques(logistiques);
        events.add(event);
        
        when(evenementRepository.findByDatedBetween(dateDebut, dateFin)).thenReturn(events);
        
        // Act
        List<Logistique> result = logistiqueService.getLogistiquesDates(dateDebut, dateFin);
        
        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).isReserve());
        verify(evenementRepository, times(1)).findByDatedBetween(dateDebut, dateFin);
        
        log.info("Test Mockito réussi - {} logistique(s) trouvée(s)", result.size());
        log.info("=== Fin du test Mockito de récupération par dates ===");
    }
}
