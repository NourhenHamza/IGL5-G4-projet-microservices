package tn.esprit.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.dto.SalleDTO;
import tn.esprit.spring.mapper.SalleMapper;
import tn.esprit.spring.persistence.entities.Salle;
import tn.esprit.spring.service.classes.SalleService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/salles")
public class SalleController {

    @Autowired
    private SalleService salleService;

    @PostMapping("/add")
    public ResponseEntity<SalleDTO> ajouter(@RequestBody SalleDTO salleDTO) {
        System.out.println("[CONTROLLER] Ajout d'une salle via API");
        Salle salle = SalleMapper.toEntity(salleDTO);
        Salle savedSalle = salleService.ajouterSalle(salle);
        SalleDTO responseDTO = SalleMapper.toDTO(savedSalle);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/update")
    public ResponseEntity<SalleDTO> modifier(@RequestBody SalleDTO salleDTO) {
        System.out.println("[CONTROLLER] Modification d'une salle via API");
        Salle salle = SalleMapper.toEntity(salleDTO);
        Salle updatedSalle = salleService.modifierSalle(salle);
        SalleDTO responseDTO = SalleMapper.toDTO(updatedSalle);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable int id) {
        System.out.println("[CONTROLLER] Suppression d'une salle via API");
        salleService.supprimerSalle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<SalleDTO>> getAll() {
        System.out.println("[CONTROLLER] Récupération de toutes les salles via API");
        List<Salle> salles = salleService.getAllSalles();
        List<SalleDTO> salleDTOs = salles.stream()
                .map(SalleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(salleDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalleDTO> getById(@PathVariable int id) {
        System.out.println("[CONTROLLER] Récupération d'une salle via API avec ID : " + id);
        Optional<Salle> salleOpt = salleService.getSalleById(id);
        if (salleOpt.isPresent()) {
            SalleDTO salleDTO = SalleMapper.toDTO(salleOpt.get());
            return ResponseEntity.ok(salleDTO);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<SalleDTO>> getSallesDisponibles() {
        System.out.println("[CONTROLLER] Récupération des salles disponibles via API");
        List<Salle> salles = salleService.getSallesDisponibles();
        List<SalleDTO> salleDTOs = salles.stream()
                .map(SalleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(salleDTOs);
    }

    @GetMapping("/capacite/{min}")
    public ResponseEntity<List<SalleDTO>> getSallesParCapacite(@PathVariable int min) {
        System.out.println("[CONTROLLER] Récupération des salles avec capacité >= " + min + " via API");
        List<Salle> salles = salleService.getSallesParCapaciteMin(min);
        List<SalleDTO> salleDTOs = salles.stream()
                .map(SalleMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(salleDTOs);
    }

    @PutMapping("/reserver/{id}")
    public ResponseEntity<SalleDTO> reserverSalle(@PathVariable int id) {
        System.out.println("[CONTROLLER] Réservation de la salle via API avec ID : " + id);
        Salle salle = salleService.reserverSalle(id);
        SalleDTO salleDTO = SalleMapper.toDTO(salle);
        return ResponseEntity.ok(salleDTO);
    }

    @PutMapping("/liberer/{id}")
    public ResponseEntity<SalleDTO> libererSalle(@PathVariable int id) {
        System.out.println("[CONTROLLER] Libération de la salle via API avec ID : " + id);
        Salle salle = salleService.libererSalle(id);
        SalleDTO salleDTO = SalleMapper.toDTO(salle);
        return ResponseEntity.ok(salleDTO);
    }
}