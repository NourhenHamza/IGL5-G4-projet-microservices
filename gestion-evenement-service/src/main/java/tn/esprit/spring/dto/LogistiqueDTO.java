package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LogistiqueDTO {
	private int idlog;
	private String description;
	private boolean reserve;
	private float prix;
	private int quantite;
}