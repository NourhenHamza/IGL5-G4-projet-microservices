package tn.esprit.spring.persistence.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Organisateur implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idOrg;

    @NotNull
    private String nom;

    @NotNull
    private String prenom;

    @Email
    private String email;

    @OneToMany(mappedBy = "organisateur")
    @JsonIgnore
    private List<Evenement> evenements = new ArrayList<>();

    // Méthode utilitaire
    public void assignerEvenement(Evenement e) {
        System.out.println("Assignation de l'événement '" + e.getDescription() + "' à l'organisateur " + nom);
        evenements.add(e);
        e.setOrganisateur(this);
    }
}
