package tn.esprit.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.persistence.entities.Salle;
import tn.esprit.spring.service.classes.SalleService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/salles")
public class SalleController {

    @Autowired
    private SalleService salleService;

    @PostMapping("/add")
    public Salle ajouter(@RequestBody Salle s) {
        System.out.println("[CONTROLLER] Ajout d'une salle via API");
        return salleService.ajouterSalle(s);
    }

    @PutMapping("/update")
    public Salle modifier(@RequestBody Salle s) {
        System.out.println("[CONTROLLER] Modification d'une salle via API");
        return salleService.modifierSalle(s);
    }

    @DeleteMapping("/delete/{id}")
    public void supprimer(@PathVariable int id) {
        System.out.println("[CONTROLLER] Suppression d'une salle via API");
        salleService.supprimerSalle(id);
    }

    @GetMapping("/all")
    public List<Salle> getAll() {
        System.out.println("[CONTROLLER] Récupération de toutes les salles via API");
        return salleService.getAllSalles();
    }

    @GetMapping("/{id}")
    public Optional<Salle> getById(@PathVariable int id) {
        System.out.println("[CONTROLLER] Récupération d'une salle via API avec ID : " + id);
        return salleService.getSalleById(id);
    }

    @GetMapping("/disponibles")
    public List<Salle> getSallesDisponibles() {
        System.out.println("[CONTROLLER] Récupération des salles disponibles via API");
        return salleService.getSallesDisponibles();
    }

    @GetMapping("/capacite/{min}")
    public List<Salle> getSallesParCapacite(@PathVariable int min) {
        System.out.println("[CONTROLLER] Récupération des salles avec capacité >= " + min + " via API");
        return salleService.getSallesParCapaciteMin(min);
    }

    @PutMapping("/reserver/{id}")
    public Salle reserverSalle(@PathVariable int id) {
        System.out.println("[CONTROLLER] Réservation de la salle via API avec ID : " + id);
        return salleService.reserverSalle(id);
    }

    @PutMapping("/liberer/{id}")
    public Salle libererSalle(@PathVariable int id) {
        System.out.println("[CONTROLLER] Libération de la salle via API avec ID : " + id);
        return salleService.libererSalle(id);
    }
}