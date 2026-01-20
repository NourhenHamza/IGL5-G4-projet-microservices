package tn.esprit.spring.salle.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalleDTO implements Serializable {
    private Integer idSalle;
    private String nom;
    private String adresse;
    private Integer capacite;
    private boolean disponible;
}