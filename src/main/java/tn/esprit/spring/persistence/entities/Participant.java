package tn.esprit.spring.persistence.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Participant implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_part")
    private int idPart;

    private String nom;
    private String prenom;

    @Enumerated(EnumType.STRING)
    private Tache tache;

    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private List<Evenement> evenements;  // ✅ Now private
}