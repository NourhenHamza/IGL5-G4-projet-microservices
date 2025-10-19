package tn.esprit.spring.service.classes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.persistence.entities.Salle;
import tn.esprit.spring.persistence.repositories.SalleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SalleService {

    @Autowired
    private SalleRepository salleRepository;

    // Ajouter une salle
    public Salle ajouterSalle(Salle s) {
        System.out.println("[SERVICE] Ajout de la salle : " + s.getNom() + " - Capacité : " + s.getCapacite());
        return salleRepository.save(s);
    }

    // Modifier une salle
    public Salle modifierSalle(Salle s) {
        System.out.println("[SERVICE] Modification de la salle : " + s.getNom() + " (ID: " + s.getIdSalle() + ")");
        return salleRepository.save(s);
    }

    // Supprimer une salle
    public void supprimerSalle(int id) {
        System.out.println("[SERVICE] Suppression de la salle avec ID : " + id);
        salleRepository.deleteById(id);
    }

    // Récupérer toutes les salles
    public List<Salle> getAllSalles() {
        System.out.println("[SERVICE] Récupération de toutes les salles");
        List<Salle> salles = salleRepository.findAll();
        System.out.println("[SERVICE] Nombre total de salles : " + salles.size());
        return salles;
    }

    // Récupérer une salle par id
    public Optional<Salle> getSalleById(int id) {
        System.out.println("[SERVICE] Récupération de la salle avec ID : " + id);
        return salleRepository.findById(id);
    }

    // Récupérer les salles disponibles
    public List<Salle> getSallesDisponibles() {
        System.out.println("[SERVICE] Récupération des salles disponibles");
        List<Salle> sallesDisponibles = salleRepository.findByDisponible(true);
        System.out.println("[SERVICE] Nombre de salles disponibles : " + sallesDisponibles.size());
        return sallesDisponibles;
    }

    // Récupérer les salles par capacité minimale
    public List<Salle> getSallesParCapaciteMin(int capacite) {
        System.out.println("[SERVICE] Recherche de salles avec capacité >= " + capacite);
        List<Salle> salles = salleRepository.findByCapaciteGreaterThanEqual(capacite);
        System.out.println("[SERVICE] Nombre de salles trouvées : " + salles.size());
        return salles;
    }

    // Réserver une salle
    public Salle reserverSalle(int id) {
        System.out.println("[SERVICE] Tentative de réservation de la salle avec ID : " + id);
        Optional<Salle> salleOpt = salleRepository.findById(id);
        if (salleOpt.isPresent()) {
            Salle salle = salleOpt.get();
            if (salle.isDisponible()) {
                salle.reserver();
                System.out.println("[SERVICE] Salle réservée avec succès : " + salle.getNom());
                return salleRepository.save(salle);
            } else {
                System.out.println("[SERVICE] ERREUR : La salle " + salle.getNom() + " n'est pas disponible");
                throw new RuntimeException("La salle n'est pas disponible");
            }
        } else {
            System.out.println("[SERVICE] ERREUR : Salle introuvable avec ID : " + id);
            throw new RuntimeException("Salle introuvable");
        }
    }

    // Libérer une salle
    public Salle libererSalle(int id) {
        System.out.println("[SERVICE] Tentative de libération de la salle avec ID : " + id);
        Optional<Salle> salleOpt = salleRepository.findById(id);
        if (salleOpt.isPresent()) {
            Salle salle = salleOpt.get();
            salle.liberer();
            System.out.println("[SERVICE] Salle libérée avec succès : " + salle.getNom());
            return salleRepository.save(salle);
        } else {
            System.out.println("[SERVICE] ERREUR : Salle introuvable avec ID : " + id);
            throw new RuntimeException("Salle introuvable");
        }
    }
}