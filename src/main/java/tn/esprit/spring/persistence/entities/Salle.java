package tn.esprit.spring.persistence.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Salle implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSalle;

    private String nom;

    private String adresse;

    private int capacite;

    private boolean disponible = true;

    @OneToMany(mappedBy = "salle")
    @JsonIgnore
    private List<Evenement> evenements = new ArrayList<>();

    public void reserver() {
        System.out.println("Réservation de la salle '" + nom + "' située à " + adresse);
        this.disponible = false;
    }

    public void liberer() {
        System.out.println("Libération de la salle '" + nom + "' située à " + adresse);
        this.disponible = true;
    }
}