package tn.esprit.spring.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.persistence.entities.Salle;

import java.util.List;

@Repository
public interface SalleRepository extends JpaRepository<Salle, Integer> {

    List<Salle> findByDisponible(boolean disponible);

    List<Salle> findByCapaciteGreaterThanEqual(int capacite);

    Salle findByNom(String nom);
}