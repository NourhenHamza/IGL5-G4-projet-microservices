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
        return salleRepository.save(s);
    }

    // Modifier une salle
    public Salle modifierSalle(Salle s) {
        return salleRepository.save(s);
    }

    // Supprimer une salle
    public void supprimerSalle(int id) {
        salleRepository.deleteById(id);
    }

    // Récupérer toutes les salles
    public List<Salle> getAllSalles() {
        List<Salle> salles = salleRepository.findAll();
        return salles;
    }

    // Récupérer une salle par id
    public Optional<Salle> getSalleById(int id) {
        return salleRepository.findById(id);
    }

    // Récupérer les salles disponibles
    public List<Salle> getSallesDisponibles() {
        List<Salle> sallesDisponibles = salleRepository.findByDisponible(true);
        return sallesDisponibles;
    }

    // Récupérer les salles par capacité minimale
    public List<Salle> getSallesParCapaciteMin(int capacite) {
        List<Salle> salles = salleRepository.findByCapaciteGreaterThanEqual(capacite);
        return salles;
    }

    // Réserver une salle
    public Salle reserverSalle(int id) {
        Optional<Salle> salleOpt = salleRepository.findById(id);
        if (salleOpt.isPresent()) {
            Salle salle = salleOpt.get();
            if (salle.isDisponible()) {
                salle.reserver();
                return salleRepository.save(salle);
            } else {
                throw new RuntimeException("La salle n'est pas disponible");
            }
        } else {
            throw new RuntimeException("Salle introuvable");
        }
    }

    // Libérer une salle
    public Salle libererSalle(int id) {
        Optional<Salle> salleOpt = salleRepository.findById(id);
        if (salleOpt.isPresent()) {
            Salle salle = salleOpt.get();
            salle.liberer();
            return salleRepository.save(salle);
        } else {
            throw new RuntimeException("Salle introuvable");
        }
    }
}