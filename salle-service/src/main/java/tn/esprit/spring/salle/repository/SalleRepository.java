package tn.esprit.spring.salle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.salle.entity.Salle;

import java.util.List;

@Repository
public interface SalleRepository extends JpaRepository<Salle, Integer> {
    List<Salle> findByDisponible(boolean disponible);
    List<Salle> findByCapaciteGreaterThanEqual(Integer capacite);
    List<Salle> findByNomContainingIgnoreCase(String nom);
}