package tn.esprit.spring.persistence.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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

    // ✅ FIXED: Made the field explicitly private to resolve SonarQube serialization issue
    // SonarQube Rule: "Make non-static 'evenements' private or transient"
    // Solution: Made it private (NOT transient) to keep JPA query functionality
    @ManyToMany(mappedBy = "participants")
    @JsonIgnore
    private List<Evenement> evenements;
}