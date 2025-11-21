package tn.esprit.spring.mapper;

import tn.esprit.spring.dto.SalleDTO;
import tn.esprit.spring.persistence.entities.Salle;

public class SalleMapper {

    public static SalleDTO toDTO(Salle salle) {
        if (salle == null) {
            return null;
        }
        return new SalleDTO(
                salle.getIdSalle(),
                salle.getNom(),
                salle.getAdresse(),
                salle.getCapacite(),
                salle.isDisponible()
        );
    }

    public static Salle toEntity(SalleDTO dto) {
        if (dto == null) {
            return null;
        }
        Salle salle = new Salle();
        if (dto.getIdSalle() != null) {
            salle.setIdSalle(dto.getIdSalle());
        }
        salle.setNom(dto.getNom());
        salle.setAdresse(dto.getAdresse());
        salle.setCapacite(dto.getCapacite());
        salle.setDisponible(dto.isDisponible());
        return salle;
    }
}