package tn.esprit.spring.service.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.persistence.entities.Organisateur;
import tn.esprit.spring.persistence.repositories.OrganisateurRepository;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

@Service
public class OrganisateurService {

    @Autowired
    private OrganisateurRepository organisateurRepository;

    // Ajouter un organisateur
    public Organisateur ajouterOrganisateur(Organisateur o){
        System.out.println("[SERVICE] Ajout de l'organisateur : " + o.getNom() + " " + o.getPrenom());
        return organisateurRepository.save(o);
    }

    // Modifier un organisateur
public Organisateur modifierOrganisateur(Organisateur o) {
    System.out.println("[SERVICE] Modification de l'organisateur : " + o.getNom() + " (ID: " + o.getIdOrg() + ")");
    
    // 1. Check if the ID exists in the incoming object
    if (o.getIdOrg() == 0) {
        throw new IllegalArgumentException("Organizer ID must be provided for modification.");
    }
    
    // 2. Fetch the existing entity from the database
    Optional<Organisateur> existingOrganizerOpt = organisateurRepository.findById(o.getIdOrg());

    if (existingOrganizerOpt.isPresent()) {
        Organisateur existingOrganizer = existingOrganizerOpt.get();
        
        // 3. Apply changes from the incoming object 'o' to the managed 'existingOrganizer'
        existingOrganizer.setNom(o.getNom());
        existingOrganizer.setPrenom(o.getPrenom());
        existingOrganizer.setEmail(o.getEmail());
        // Add more setters here for every field that can be updated
        
        // 4. Save the managed entity (this will execute an UPDATE)
        return organisateurRepository.save(existingOrganizer);
    } else {
        // Handle case where ID is provided but doesn't exist
        throw new EntityNotFoundException("Organizer with ID " + o.getIdOrg() + " not found for update.");
    }
}
    // Supprimer un organisateur par id
    public void supprimerOrganisateur(int id){
        System.out.println("[SERVICE] Suppression de l'organisateur avec ID : " + id);
        organisateurRepository.deleteById(id);
    }

    // Récupérer tous les organisateurs
    public List<Organisateur> getAllOrganisateurs(){
        System.out.println("[SERVICE] Récupération de tous les organisateurs");
        return organisateurRepository.findAll();
    }

    // Récupérer un organisateur par id
    public Optional<Organisateur> getOrganisateurById(int id){
        System.out.println("[SERVICE] Récupération de l'organisateur avec ID : " + id);
        return organisateurRepository.findById(id);
    }
}
