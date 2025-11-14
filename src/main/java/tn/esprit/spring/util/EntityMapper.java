package tn.esprit.spring.util;

import tn.esprit.spring.dto.EvenementDTO;
import tn.esprit.spring.dto.LogistiqueDTO;
import tn.esprit.spring.dto.ParticipantDTO;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Logistique;
import tn.esprit.spring.persistence.entities.Participant;

public class EntityMapper {

    // Private constructor to hide the implicit public one
    private EntityMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ParticipantDTO toDTO(Participant participant) {
        if (participant == null) return null;
        return new ParticipantDTO(
                participant.getIdPart(),
                participant.getNom(),
                participant.getPrenom(),
                participant.getTache()
        );
    }

    public static Participant toEntity(ParticipantDTO dto) {
        if (dto == null) return null;
        Participant participant = new Participant();
        participant.setIdPart(dto.getId());
        participant.setNom(dto.getNom());
        participant.setPrenom(dto.getPrenom());
        participant.setTache(dto.getTache());
        return participant;
    }

    public static EvenementDTO toDTO(Evenement evenement) {
        if (evenement == null) return null;
        return new EvenementDTO(
                evenement.getId(),
                evenement.getDescription(),
                evenement.getDated(),
                evenement.getCout(),
                evenement.getParticipants() != null ?
                        evenement.getParticipants().stream().map(Participant::getIdPart).toList() : null,
                evenement.getLogistiques() != null ?
                        evenement.getLogistiques().stream().map(Logistique::getIdlog).toList() : null
        );
    }

    public static Evenement toEntity(EvenementDTO dto) {
        if (dto == null) return null;
        Evenement evenement = new Evenement();
        evenement.setId(dto.getId());
        evenement.setDescription(dto.getDescription());
        evenement.setDated(dto.getDated());
        evenement.setCout(dto.getCout());
        return evenement;
    }

    public static LogistiqueDTO toDTO(Logistique logistique) {
        if (logistique == null) return null;
        return new LogistiqueDTO(
                logistique.getIdlog(),
                logistique.getDescription(),
                logistique.isReserve(),
                logistique.getPrix(),
                logistique.getQuantite()
        );
    }

    public static Logistique toEntity(LogistiqueDTO dto) {
        if (dto == null) return null;
        Logistique logistique = new Logistique();
        logistique.setIdlog(dto.getId());
        logistique.setDescription(dto.getDescription());
        logistique.setReserve(dto.isReserve());
        logistique.setPrix(dto.getPrixUnit());
        logistique.setQuantite(dto.getQuantite());
        return logistique;
    }
}