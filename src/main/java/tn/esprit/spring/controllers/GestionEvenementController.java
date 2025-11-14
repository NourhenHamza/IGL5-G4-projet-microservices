package tn.esprit.spring.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tn.esprit.spring.dto.EvenementDTO;
import tn.esprit.spring.dto.LogistiqueDTO;
import tn.esprit.spring.dto.ParticipantDTO;
import tn.esprit.spring.service.interfaces.IEvenemntService;
import tn.esprit.spring.service.interfaces.ILogistiqueService;
import tn.esprit.spring.service.interfaces.IParticipantService;
import tn.esprit.spring.util.EntityMapper;

@RestController
@RequestMapping("/Evenement")
@RequiredArgsConstructor
public class GestionEvenementController {

    private final IEvenemntService evenServ;
    private final IParticipantService partServ;
    private final ILogistiqueService logisServ;

    // Question 1
    // http://localhost:8082/GestionEvenement/Evenement/add-Participant
    @PostMapping("/add-Participant")
    public ParticipantDTO addParticipant(@RequestBody ParticipantDTO participantDTO) {
        var participant = EntityMapper.toEntity(participantDTO);
        partServ.ajouterParticipant(participant);
        return EntityMapper.toDTO(participant);
    }

    // Question 2 - 1ère signature
    // http://localhost:8082/GestionEvenement/Evenement/add-Affect-Event/1
    @PostMapping("/add-Affect-Event/{idParticip}")
    public EvenementDTO addAffectEventParticipant(@RequestBody EvenementDTO evenementDTO,
                                                  @PathVariable("idParticip") int idParticip) {
        var evenement = EntityMapper.toEntity(evenementDTO);
        evenServ.ajoutAffectEvenParticip(evenement, idParticip);
        return EntityMapper.toDTO(evenement);
    }

    // Question 2 - 2ère signature
    @PostMapping("/add-Affect-Event-To-Participant")
    public EvenementDTO addAffectEventParticipant(@RequestBody EvenementDTO evenementDTO) {
        var evenement = EntityMapper.toEntity(evenementDTO);
        evenServ.ajoutAffectEvenParticip(evenement);
        return EntityMapper.toDTO(evenement);
    }

    // Question 3
    // http://localhost:8082/GestionEvenement/Evenement/add-Affect-LogEvent/Festival Medina
    @PostMapping("/add-Affect-LogEvent/{descript}")
    public LogistiqueDTO addAffectLogEvnm(@RequestBody LogistiqueDTO logistiqueDTO,
                                          @PathVariable("descript") String descriptionEvnm) {
        var logistique = EntityMapper.toEntity(logistiqueDTO);
        logisServ.ajoutAffectLogEven(logistique, descriptionEvnm);
        return EntityMapper.toDTO(logistique);
    }

    // Question 4
    // http://localhost:8082/GestionEvenement/Evenement/retrieveLogistiquesDates/2023-01-01/2023-06-01
    @GetMapping("/retrieveLogistiquesDates/{dateD}/{dateF}")
    public List<LogistiqueDTO> retrieveLogistiquesDates(
            @PathVariable("dateD") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateDb,
            @PathVariable("dateF") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFin) {
        return logisServ.getLogistiquesDates(dateDb, dateFin)
                .stream()
                .map(EntityMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Question 5
    // http://localhost:8082/GestionEvenement/Evenement/getParticipantsLogis
    @GetMapping("/getParticipantsLogis")
    public List<ParticipantDTO> getParReservLogis() {
        return partServ.getParReservLogis()
                .stream()
                .map(EntityMapper::toDTO)
                .collect(Collectors.toList());
    }
}