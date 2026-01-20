package tn.esprit.spring.salle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.salle.entity.Salle;
import tn.esprit.spring.salle.repository.SalleRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SalleService {

    private final SalleRepository salleRepository;

    public List<Salle> getAllSalles() {
        log.info("Fetching all salles");
        return salleRepository.findAll();
    }

    public Optional<Salle> getSalleById(Integer id) {
        log.info("Fetching salle with id: {}", id);
        return salleRepository.findById(id);
    }

    public Salle ajouterSalle(Salle salle) {
        log.info("Creating salle: {}", salle.getNom());
        return salleRepository.save(salle);
    }

    public Salle modifierSalle(Salle salle) {
        log.info("Updating salle with id: {}", salle.getIdSalle());
        return salleRepository.save(salle);
    }

    public void supprimerSalle(Integer id) {
        log.info("Deleting salle with id: {}", id);
        salleRepository.deleteById(id);
    }

    public List<Salle> getSallesDisponibles() {
        log.info("Fetching available salles");
        return salleRepository.findByDisponible(true);
    }

    public List<Salle> getSallesParCapaciteMin(Integer capaciteMin) {
        log.info("Fetching salles with capacity >= {}", capaciteMin);
        return salleRepository.findByCapaciteGreaterThanEqual(capaciteMin);
    }

    public Salle reserverSalle(Integer id) {
        log.info("Reserving salle with id: {}", id);
        Salle salle = salleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salle introuvable"));

        
        if (!salle.isDisponible()) {
            throw new RuntimeException("La salle n'est pas disponible");
        }
        
        salle.reserver();
        return salleRepository.save(salle);
    }

    public Salle libererSalle(Integer id) {
        log.info("Freeing salle with id: {}", id);
        Salle salle = salleRepository.findById(id)
               .orElseThrow(() -> new RuntimeException("Salle introuvable"));

        
        salle.liberer();
        return salleRepository.save(salle);
    }
}