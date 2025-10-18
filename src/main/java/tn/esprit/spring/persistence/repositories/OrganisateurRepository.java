package tn.esprit.spring.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.persistence.entities.Organisateur;

@Repository
public interface OrganisateurRepository extends JpaRepository<Organisateur, Integer> {
}
