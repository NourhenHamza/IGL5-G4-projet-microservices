package tn.esprit.spring.service.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.persistence.entities.Organisateur;
import tn.esprit.spring.persistence.repositories.OrganisateurRepository;

import java.util.List;
import java.util.Optional;

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
    public Organisateur modifierOrganisateur(Organisateur o){
        System.out.println("[SERVICE] Modification de l'organisateur : " + o.getNom());
        return organisateurRepository.save(o);
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
