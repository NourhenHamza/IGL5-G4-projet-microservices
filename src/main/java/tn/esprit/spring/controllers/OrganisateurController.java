package tn.esprit.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.persistence.entities.Organisateur;
import tn.esprit.spring.service.classes.OrganisateurService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/organisateurs")
public class OrganisateurController {

    @Autowired
    private OrganisateurService organisateurService;

    @PostMapping("/add")
    public Organisateur ajouter(@RequestBody Organisateur o){
        System.out.println("[CONTROLLER] Ajout d'un organisateur via API");
        return organisateurService.ajouterOrganisateur(o);
    }

    @PutMapping("/update")
    public Organisateur modifier(@RequestBody Organisateur o){
        System.out.println("[CONTROLLER] Modification d'un organisateur via API");
        return organisateurService.modifierOrganisateur(o);
    }

    @DeleteMapping("/delete/{id}")
    public void supprimer(@PathVariable int id){
        System.out.println("[CONTROLLER] Suppression d'un organisateur via API");
        organisateurService.supprimerOrganisateur(id);
    }

    @GetMapping("/all")
    public List<Organisateur> getAll(){
        System.out.println("[CONTROLLER] Récupération de tous les organisateurs via API");
        return organisateurService.getAllOrganisateurs();
    }

    @GetMapping("/{id}")
    public Optional<Organisateur> getById(@PathVariable int id){
        System.out.println("[CONTROLLER] Récupération d'un organisateur via API avec ID : " + id);
        return organisateurService.getOrganisateurById(id);
    }
}
