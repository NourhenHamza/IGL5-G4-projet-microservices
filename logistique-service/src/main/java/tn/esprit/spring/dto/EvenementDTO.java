package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvenementDTO {
    private Long id;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private String lieu;
    private int capacite;
}