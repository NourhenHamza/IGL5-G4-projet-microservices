package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvenementDTO {
    private int id;
    private String description;
    private Date dated;
    private float cout;
    private List<Integer> participantIds;
    private List<Integer> logistiqueIds;
}