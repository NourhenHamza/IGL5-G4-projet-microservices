package tn.esprit.spring.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Logistique;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.service.interfaces.IEvenemntService;
import tn.esprit.spring.service.interfaces.ILogistiqueService;
import tn.esprit.spring.service.interfaces.IParticipantService;

@RestController
@RequestMapping("/Evenement")
@Slf4j
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
	public Participant addParticipant(@RequestBody Participant p) {
		log.info("=== Requête POST reçue: /Evenement/add-Participant ===");
		log.info("Ajout d'un nouveau participant: {} {}, Tâche: {}",
				p.getNom(), p.getPrenom(), p.getTache());

		try {
			partServ.ajouterParticipant(p);
			log.info("Participant ajouté avec succès - ID: {}", p.getIdPart());
			return p;
		} catch (Exception e) {
			log.error("Erreur lors de l'ajout du participant: {} {}", p.getNom(), p.getPrenom(), e);
			throw e;
		}
	}

	//Question 2- 1ère signature
	//http://localhost:8082/GestionEvenement/Evenement/add-Affect-Event/1
	@PostMapping("/add-Affect-Event/{idParticip}")
	public Evenement addAffectEventParticipant(@RequestBody Evenement e,
											   @PathVariable("idParticip") int idParticip) {
		log.info("=== Requête POST reçue: /Evenement/add-Affect-Event/{} ===", idParticip);
		log.info("Tentative d'ajout et affectation d'événement '{}' au participant ID: {}",
				e.getDescription(), idParticip);

		try {
			evenServ.ajoutAffectEvenParticip(e, idParticip);
			log.info("Événement '{}' ajouté et affecté avec succès au participant ID: {}",
					e.getDescription(), idParticip);
			return e;
		} catch (Exception ex) {
			log.error("Erreur lors de l'ajout/affectation de l'événement au participant ID: {}",
					idParticip, ex);
			throw ex;
		}
	}

	//Question 2- 2ème signature
	@PostMapping("/add-Affect-Event-To-Participant")
	public Evenement addAffectEventParticipant(@RequestBody Evenement e) {
		log.info("=== Requête POST reçue: /Evenement/add-Affect-Event-To-Participant ===");
		log.info("Ajout d'un nouvel événement: '{}' avec {} participant(s) pré-affecté(s)",
				e.getDescription(),
				e.getParticipants() != null ? e.getParticipants().size() : 0);

		try {
			evenServ.ajoutAffectEvenParticip(e);
			log.info("Événement '{}' créé avec succès - ID: {}", e.getDescription(), e.getId());
			return e;
		} catch (Exception ex) {
			log.error("Erreur lors de l'ajout de l'événement: {}", e.getDescription(), ex);
			throw ex;
		}
	}

	//Question 3
	//http://localhost:8082/GestionEvenement/Evenement/add-Affect-LogEvent/Festival Medina
	@PostMapping("/add-Affect-LogEvent/{descript}")
	public Logistique addAffectLogEvnm(@RequestBody Logistique l,
									   @PathVariable("descript") String description_evnm) {
		log.info("=== Requête POST reçue: /Evenement/add-Affect-LogEvent/{} ===", description_evnm);
		log.info("Ajout de logistique '{}' (Prix: {}, Quantité: {}) à l'événement '{}'",
				l.getDescription(), l.getPrix(), l.getQuantite(), description_evnm);

		try {
			logisServ.ajoutAffectLogEven(l, description_evnm);
			log.info("Logistique '{}' ajoutée avec succès (ID: {}) à l'événement '{}'",
					l.getDescription(), l.getIdlog(), description_evnm);
			return l;
		} catch (Exception e) {
			log.error("Erreur lors de l'ajout de la logistique à l'événement: {}",
					description_evnm, e);
			throw e;
		}
	}

	//Question 4
	//http://localhost:8082/GestionEvenement/Evenement/retrieveLogistiquesDates/2023-01-01/2023-06-01
	@GetMapping("/retrieveLogistiquesDates/{dateD}/{dateF}")
	public List<Logistique> retrieveLogistiquesDates(
			@PathVariable("dateD") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateDb,
			@PathVariable("dateF") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date dateFin) {

		log.info("=== Requête GET reçue: /Evenement/retrieveLogistiquesDates/{}/{} ===", dateDb, dateFin);
		log.info("Recherche des logistiques réservées entre {} et {}", dateDb, dateFin);

		try {
			List<Logistique> logistiques = logisServ.getLogistiquesDates(dateDb, dateFin);
			log.info("Nombre de logistiques réservées trouvées: {}", logistiques.size());

			if (logistiques.isEmpty()) {
				log.warn("Aucune logistique réservée trouvée pour la période {} - {}", dateDb, dateFin);
			}

			return logistiques;
		} catch (Exception e) {
			log.error("Erreur lors de la récupération des logistiques pour la période {} - {}",
					dateDb, dateFin, e);
			throw e;
		}
	}

	//Question 5
	//http://localhost:8082/GestionEvenement/Evenement/getParticipantsLogis
	@GetMapping("/getParticipantsLogis")
	public List<Participant> getParReservLogis() {
		log.info("=== Requête GET reçue: /Evenement/getParticipantsLogis ===");
		log.info("Recherche des participants ORGANISATEUR ayant réservé des logistiques");

		try {
			List<Participant> participants = partServ.getParReservLogis();
			log.info("Nombre de participants organisateurs avec logistiques réservées: {}",
					participants.size());

			if (participants.isEmpty()) {
				log.warn("Aucun participant organisateur avec logistiques réservées trouvé");
			} else {
				for (Participant p : participants) {
					log.debug("Participant trouvé: {} {} (ID: {})",
							p.getNom(), p.getPrenom(), p.getIdPart());
				}
			}

			return participants;
		} catch (Exception e) {
			log.error("Erreur lors de la récupération des participants avec logistiques", e);
			throw e;
		}
	}
}