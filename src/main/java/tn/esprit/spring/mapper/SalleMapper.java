package tn.esprit.spring.mapper;

import tn.esprit.spring.dto.SalleDTO;
import tn.esprit.spring.persistence.entities.Salle;

/**
 * Mapper to convert between Salle entity and SalleDTO
 * This ensures separation between persistence layer and API layer
 */
public class SalleMapper {

    private SalleMapper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Convert Salle entity to SalleDTO
     * @param salle The entity to convert
     * @return SalleDTO or null if input is null
     */
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

    /**
     * Convert SalleDTO to Salle entity
     * @param dto The DTO to convert
     * @return Salle entity or null if input is null
     */
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