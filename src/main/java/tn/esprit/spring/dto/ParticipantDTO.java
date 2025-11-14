package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.spring.persistence.entities.Tache;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDTO {
    private int id;
    private String nom;
    private String prenom;
    private Tache tache;
}