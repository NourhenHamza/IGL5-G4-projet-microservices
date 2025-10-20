package tn.esprit.spring.service.classes;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.persistence.repositories.EvenementRepository;
import tn.esprit.spring.persistence.repositories.ParticipantRepository;
import tn.esprit.spring.service.interfaces.IEvenemntService;

@Service
@Slf4j
public class EvenementServiceImpl implements IEvenemntService {

	@Autowired
	ParticipantRepository partRep;

	@Autowired
	EvenementRepository evenRep;

	// 1ère méthode d'affectation avec la signature Evenement ajoutAffectEvenParticip(Evenement e, int idParticip)
	@Override
	public Evenement ajoutAffectEvenParticip(Evenement e, int idParticip) {
		log.info("Début d'ajout et affectation d'un événement au participant avec ID: {}", idParticip);

		try {
			// Récupération du participant
			Participant p = partRep.findById(idParticip).get();
			log.info("Participant trouvé: {} {}", p.getNom(), p.getPrenom());

			// Récupération de l'événement sauvegardé
			Evenement savedEvent = evenRep.findById(e.getId()).get();
			log.info("Événement existant trouvé avec ID: {}", savedEvent.getId());

			List<Participant> pts;

			if (savedEvent.getParticipants() == null) {
				log.info("Initialisation de la liste des participants pour l'événement ID: {}", savedEvent.getId());
				pts = new ArrayList<>();
			} else {
				pts = savedEvent.getParticipants();
				log.info("Nombre de participants existants: {}", pts.size());
			}

			pts.add(p);
			e.setParticipants(pts);

			evenRep.save(e);
			log.info("Événement '{}' sauvegardé avec succès. Total participants: {}",
					e.getDescription(), pts.size());

			return e;

		} catch (Exception ex) {
			log.error("Erreur lors de l'ajout/affectation de l'événement au participant ID: {}", idParticip, ex);
			throw ex;
		}
	}

	// 2ème méthode d'affectation avec la signature: Evenement ajoutAffectEvenParticip(Evenement e)
	@Override
	public Evenement ajoutAffectEvenParticip(Evenement e) {
		log.info("Début d'ajout d'un nouvel événement: {}", e.getDescription());

		try {
			if (e.getParticipants() != null && !e.getParticipants().isEmpty()) {
				log.info("L'événement contient {} participant(s) pré-affecté(s)", e.getParticipants().size());

				for (Participant p : e.getParticipants()) {
					log.debug("Participant: {} {} - Tâche: {}",
							p.getNom(), p.getPrenom(), p.getTache());
				}
			} else {
				log.warn("Aucun participant affecté à l'événement '{}'", e.getDescription());
			}

			evenRep.save(e);
			log.info("Événement '{}' ajouté avec succès (ID: {}). Date début: {}, Date fin: {}",
					e.getDescription(), e.getId(), e.getDated(), e.getDatef());

			return e;

		} catch (Exception ex) {
			log.error("Erreur lors de l'ajout de l'événement: {}", e.getDescription(), ex);
			throw ex;
		}
	}
}