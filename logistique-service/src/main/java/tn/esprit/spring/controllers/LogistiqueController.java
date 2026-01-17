package tn.esprit.spring.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.dto.LogistiqueDTO;
import tn.esprit.spring.persistence.entities.Logistique;
import tn.esprit.spring.service.interfaces.ILogistiqueService;

/**
 * Controller REST dédié à la gestion de la Logistique
 * Microservice autonome et modulaire
 * 
 * Endpoints exposés :
 * - POST /logistique/add-affect/{description} : Ajouter et affecter une logistique
 * - GET /logistique/by-dates : Récupérer les logistiques par période
 * - GET /logistique/reserved : Récupérer toutes les logistiques réservées
 * 
 * @author Votre Nom
 * @version 1.0
 */
@RestController
@RequestMapping("/logistique")
@Api(tags = "Gestion Logistique", description = "API de gestion de la logistique des événements")
@Slf4j
@CrossOrigin(origins = "*")
public class LogistiqueController {

    @Autowired
    private ILogistiqueService logistiqueService;

    /**
     * Ajouter et affecter une logistique à un événement
     * 
     * @param logistiqueDTO DTO de la logistique
     * @param descriptionEvent Description de l'événement
     * @return LogistiqueDTO créée
     */
    @PostMapping("/add-affect/{description}")
    @ApiOperation(value = "Ajouter et affecter une logistique à un événement")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Logistique créée et affectée avec succès"),
        @ApiResponse(code = 404, message = "Événement non trouvé"),
        @ApiResponse(code = 500, message = "Erreur serveur")
    })
    public ResponseEntity<LogistiqueDTO> addAffectLogistique(
            @RequestBody LogistiqueDTO logistiqueDTO,
            @PathVariable("description") String descriptionEvent) {
        
        log.info("Requête reçue : Ajouter logistique pour événement '{}'", descriptionEvent);
        
        try {
            Logistique logistique = convertToEntity(logistiqueDTO);
            Logistique savedLogistique = logistiqueService.ajoutAffectLogEven(logistique, descriptionEvent);
            
            if (savedLogistique == null) {
                log.error("Événement non trouvé : {}", descriptionEvent);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            log.info("Logistique créée avec succès - ID: {}", savedLogistique.getIdlog());
            return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedLogistique));
            
        } catch (Exception e) {
            log.error("Erreur lors de la création de la logistique", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupérer les logistiques réservées entre deux dates
     * 
     * @param dateDebut Date de début
     * @param dateFin Date de fin
     * @return Liste des logistiques réservées
     */
    @GetMapping("/by-dates")
    @ApiOperation(value = "Récupérer les logistiques réservées par période")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Liste récupérée avec succès"),
        @ApiResponse(code = 400, message = "Paramètres de date invalides"),
        @ApiResponse(code = 500, message = "Erreur serveur")
    })
    public ResponseEntity<List<LogistiqueDTO>> getLogistiquesByDates(
            @RequestParam("dateDebut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateDebut,
            @RequestParam("dateFin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFin) {
        
        log.info("Requête reçue : Récupérer logistiques entre {} et {}", dateDebut, dateFin);
        
        try {
            if (dateDebut.after(dateFin)) {
                log.error("Date de début postérieure à la date de fin");
                return ResponseEntity.badRequest().build();
            }
            
            List<LogistiqueDTO> logistiques = logistiqueService.getLogistiquesDates(dateDebut, dateFin)
                    .stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            log.info("{} logistiques trouvées", logistiques.size());
            return ResponseEntity.ok(logistiques);
            
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des logistiques", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Health check du service Logistique
     * 
     * @return Status du service
     */
    @GetMapping("/health")
    @ApiOperation(value = "Vérifier l'état du service Logistique")
    public ResponseEntity<String> healthCheck() {
        log.debug("Health check appelé");
        return ResponseEntity.ok("Service Logistique actif et opérationnel");
    }

    // ==================== Méthodes de conversion ====================

    private LogistiqueDTO convertToDTO(Logistique logistique) {
        LogistiqueDTO dto = new LogistiqueDTO();
        dto.setIdlog(logistique.getIdlog());
        dto.setDescription(logistique.getDescription());
        dto.setReserve(logistique.isReserve());
        dto.setPrix(logistique.getPrix());
        dto.setQuantite(logistique.getQuantite());
        return dto;
    }

    private Logistique convertToEntity(LogistiqueDTO dto) {
        Logistique logistique = new Logistique();
        logistique.setIdlog(dto.getIdlog());
        logistique.setDescription(dto.getDescription());
        logistique.setReserve(dto.isReserve());
        logistique.setPrix(dto.getPrix());
        logistique.setQuantite(dto.getQuantite());
        return logistique;
    }
}