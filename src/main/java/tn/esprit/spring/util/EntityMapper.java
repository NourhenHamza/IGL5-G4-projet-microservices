package tn.esprit.spring.util;

import tn.esprit.spring.dto.EvenementDTO;
import tn.esprit.spring.dto.LogistiqueDTO;
import tn.esprit.spring.dto.ParticipantDTO;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Logistique;
import tn.esprit.spring.persistence.entities.Participant;

import java.util.stream.Collectors;

public class EntityMapper {

    public static ParticipantDTO toDTO(Participant participant) {
        if (participant == null) return null;
        return new ParticipantDTO(
                participant.getIdPart(),  // Changed from getId()
                participant.getNom(),
                participant.getPrenom(),
                participant.getTache()
        );
    }

    public static Participant toEntity(ParticipantDTO dto) {
        if (dto == null) return null;
        Participant participant = new Participant();
        participant.setIdPart(dto.getId());  // Changed from setId()
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
                        evenement.getParticipants().stream().map(Participant::getIdPart).collect(Collectors.toList()) : null,  // Changed from getId()
                evenement.getLogistiques() != null ?
                        evenement.getLogistiques().stream().map(Logistique::getIdlog).collect(Collectors.toList()) : null  // Changed from getId()
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
                logistique.getIdlog(),  // Changed from getId()
                logistique.getDescription(),
                logistique.isReserve(),
                logistique.getPrix(),  // Changed from getPrixUnit()
                logistique.getQuantite()
        );
    }

    public static Logistique toEntity(LogistiqueDTO dto) {
        if (dto == null) return null;
        Logistique logistique = new Logistique();
        logistique.setIdlog(dto.getId());  // Changed from setId()
        logistique.setDescription(dto.getDescription());
        logistique.setReserve(dto.isReserve());
        logistique.setPrix(dto.getPrixUnit());  // Changed from setPrixUnit()
        logistique.setQuantite(dto.getQuantite());
        return logistique;
    }
}