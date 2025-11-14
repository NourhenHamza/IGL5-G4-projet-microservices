package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogistiqueDTO {
    private int id;
    private String description;
    private boolean reserve;
    private float prixUnit;
    private int quantite;
}