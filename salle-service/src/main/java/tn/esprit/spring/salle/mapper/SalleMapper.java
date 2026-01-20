package tn.esprit.spring.salle.mapper;

import tn.esprit.spring.salle.dto.SalleDTO;
import tn.esprit.spring.salle.entity.Salle;

public class SalleMapper {

    public static SalleDTO toDTO(Salle salle) {
        if (salle == null) {
            return null;
        }
        
        return SalleDTO.builder()
                .idSalle(salle.getIdSalle())
                .nom(salle.getNom())
                .adresse(salle.getAdresse())
                .capacite(salle.getCapacite())
                .disponible(salle.isDisponible())
                .build();
    }

    public static Salle toEntity(SalleDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Salle.builder()
                .idSalle(dto.getIdSalle())
                .nom(dto.getNom())
                .adresse(dto.getAdresse())
                .capacite(dto.getCapacite())
                .disponible(dto.isDisponible())
                .build();
    }
}