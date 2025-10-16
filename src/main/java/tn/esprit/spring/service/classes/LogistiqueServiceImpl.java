package tn.esprit.spring.service.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Logistique;
import tn.esprit.spring.persistence.repositories.EvenementRepository;
import tn.esprit.spring.persistence.repositories.LogistiqueRepository;
import tn.esprit.spring.service.interfaces.ILogistiqueService;

@Service
@Slf4j
public class LogistiqueServiceImpl implements ILogistiqueService {
	@Autowired
	EvenementRepository evenRep;
	@Autowired
	LogistiqueRepository logistiqueRepository;
	
	
	@Override
	public Logistique ajoutAffectLogEven(Logistique l, String description_evnm) {
		log.info("Début de l'ajout et affectation de logistique pour l'événement: {}", description_evnm);
		
		Evenement e = evenRep.findByDescription(description_evnm);
		
		if (e == null) {
			log.error("Événement non trouvé avec la description: {}", description_evnm);
			return null;
		}
		
		log.info("Événement trouvé: {} - ID: {}", e.getDescription(), e.getId());
		
		List<Logistique> logis;
		if (e.getLogistiques() == null) {
			log.info("Aucune logistique existante pour cet événement, création d'une nouvelle liste");
			logis = new ArrayList<>();
		} else {
			log.info("Liste de logistiques existante trouvée avec {} élément(s)", e.getLogistiques().size());
			logis = e.getLogistiques();
		}
		
		logistiqueRepository.save(l);
		log.info("Logistique sauvegardée avec succès - ID: {}", l.getId());
		
		logis.add(l);
		e.setLogistiques(logis);
		evenRep.save(e);
		
		log.info("Logistique affectée à l'événement avec succès. Total logistiques: {}", logis.size());
		return l;
	}

	@Override
	public List<Logistique> getLogistiquesDates(Date dated, Date datef) {
		log.info("Récupération des logistiques réservées entre {} et {}", dated, datef);
		
		List<Evenement> events = evenRep.findByDatedBetween(dated, datef);
		log.info("Nombre d'événements trouvés dans cette période: {}", events.size());
		
		List<Logistique> allLogists = new ArrayList<>();
		int totalLogistiques = 0;
		int logistiquesReservees = 0;
		
		for (Evenement e : events) {
			log.debug("Traitement de l'événement: {} - Date: {}", e.getDescription(), e.getDated());
			
			if (e.getLogistiques() != null) {
				totalLogistiques += e.getLogistiques().size();
				
				for (Logistique l : e.getLogistiques()) {
					if (l.isReserve()) {
						allLogists.add(l);
						logistiquesReservees++;
						log.debug("Logistique réservée ajoutée - ID: {}", l.getId());
					}
				}
			}
		}
		
		log.info("Résultat: {} logistiques réservées trouvées sur {} logistiques totales", 
				logistiquesReservees, totalLogistiques);
		return allLogists;
	}
}