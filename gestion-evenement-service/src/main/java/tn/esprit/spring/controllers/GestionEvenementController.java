package tn.esprit.spring.controllers;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tn.esprit.spring.dto.EvenementDTO;
import tn.esprit.spring.dto.LogistiqueDTO;
import tn.esprit.spring.dto.ParticipantDTO;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Logistique;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.persistence.entities.Tache;
import tn.esprit.spring.service.interfaces.IEvenemntService;
import tn.esprit.spring.service.interfaces.ILogistiqueService;
import tn.esprit.spring.service.interfaces.IParticipantService;

@RestController
@RequestMapping("/Evenement")
public class GestionEvenementController {

	@Autowired
	IEvenemntService evenServ;
	@Autowired
	IParticipantService partServ;
	@Autowired
	ILogistiqueService logisServ;
	
	//Question 1
	//http://localhost:8082/GestionEvenement/Evenement/add-Participant
	@PostMapping("/add-Participant")
	public ParticipantDTO addParticipant(@RequestBody ParticipantDTO pDto) {
		Participant p = convertToEntity(pDto);
		partServ.ajouterParticipant(p);
		return convertToDTO(p);
	}
	
	//Question 2- 1ère signature
	//http://localhost:8082/GestionEvenement/Evenement/add-Affect-Event/1
	@PostMapping("/add-Affect-Event/{idParticip}")
	public EvenementDTO addAffectEventParticipant(@RequestBody EvenementDTO eDto, @PathVariable("idParticip") int idParticip) {
		Evenement e = convertToEntity(eDto);
		evenServ.ajoutAffectEvenParticip(e, idParticip);
		return convertToDTO(e);
	}
	
	//Question 2- 2ère signature
	@PostMapping("/add-Affect-Event-To-Participant")
	public EvenementDTO addAffectEventParticipant(@RequestBody EvenementDTO eDto) {
		Evenement e = convertToEntity(eDto);
		evenServ.ajoutAffectEvenParticip(e);
		return convertToDTO(e);
	}
	
	//Question 3
	//http://localhost:8082/GestionEvenement/Evenement/add-Affect-LogEvent/Festival Medina
	@PostMapping("/add-Affect-LogEvent/{descript}")
	public LogistiqueDTO addAffectLogEvnm(@RequestBody LogistiqueDTO lDto, @PathVariable("descript") String descriptionEvnm) {
		Logistique l = convertToEntity(lDto);
		logisServ.ajoutAffectLogEven(l, descriptionEvnm);
		return convertToDTO(l);
	}
	
	//Question 4
	//http://localhost:8082/GestionEvenement/Evenement/retrieveLogistiquesDates/2023-01-01/2023-06-01
	@GetMapping("/retrieveLogistiquesDates/{dateD}/{dateF}")
	public List<LogistiqueDTO> retrieveLogistiquesDates(
			@PathVariable("dateD") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateDb, 
			@PathVariable("dateF") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFin) {
		
		return logisServ.getLogistiquesDates(dateDb, dateFin)
				.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}
	
	//Question 5
	//http://localhost:8082/GestionEvenement/Evenement/retrieveLogistiquesDates/2023-01-01/2023-06-01
	@GetMapping("/getParticipantsLogis")
	public List<ParticipantDTO> getParReservLogis() {
		return partServ.getParReservLogis()
				.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}
	
	// Conversion methods
	private ParticipantDTO convertToDTO(Participant p) {
		ParticipantDTO dto = new ParticipantDTO();
		dto.setIdParticipant(p.getIdPart()); // Fixed: was getIdParticipant()
		dto.setNom(p.getNom());
		dto.setPrenom(p.getPrenom());
		// Fixed: Tache is an enum, convert to String
		dto.setTache(p.getTache() != null ? p.getTache().name() : null);
		return dto;
	}
	
	private Participant convertToEntity(ParticipantDTO dto) {
		Participant p = new Participant();
		p.setIdPart(dto.getIdParticipant()); // Fixed: was setIdParticipant()
		p.setNom(dto.getNom());
		p.setPrenom(dto.getPrenom());
		// Fixed: Convert String to Tache enum
		if (dto.getTache() != null && !dto.getTache().isEmpty()) {
			p.setTache(Tache.valueOf(dto.getTache()));
		}
		return p;
	}
	
	private EvenementDTO convertToDTO(Evenement e) {
		EvenementDTO dto = new EvenementDTO();
		dto.setId(e.getId());
		dto.setDescription(e.getDescription());
		dto.setDated(e.getDated());
		dto.setDatef(e.getDatef());
		dto.setCout(e.getCout());
		return dto;
	}
	
	private Evenement convertToEntity(EvenementDTO dto) {
		Evenement e = new Evenement();
		e.setId(dto.getId());
		e.setDescription(dto.getDescription());
		e.setDated(dto.getDated());
		e.setDatef(dto.getDatef());
		e.setCout(dto.getCout());
		return e;
	}
	
	private LogistiqueDTO convertToDTO(Logistique l) {
		LogistiqueDTO dto = new LogistiqueDTO();
		dto.setIdlog(l.getIdlog());
		dto.setDescription(l.getDescription());
		dto.setReserve(l.isReserve());
		dto.setPrix(l.getPrix());
		dto.setQuantite(l.getQuantite());
		return dto;
	}
	
	private Logistique convertToEntity(LogistiqueDTO dto) {
		Logistique l = new Logistique();
		l.setIdlog(dto.getIdlog());
		l.setDescription(dto.getDescription());
		l.setReserve(dto.isReserve());
		l.setPrix(dto.getPrix());
		l.setQuantite(dto.getQuantite());
		return l;
	}
}