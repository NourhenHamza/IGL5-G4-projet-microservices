package tn.esprit.spring.service.classes;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Logistique;
import tn.esprit.spring.persistence.entities.Participant;
import tn.esprit.spring.persistence.entities.Tache;
import tn.esprit.spring.persistence.repositories.EvenementRepository;
import tn.esprit.spring.persistence.repositories.LogistiqueRepository;
import tn.esprit.spring.persistence.repositories.ParticipantRepository;
import tn.esprit.spring.service.interfaces.IParticipantService;

@Service
@Slf4j
public class ParticipantServiceImpl implements IParticipantService {

    @Autowired
    ParticipantRepository partRep;

    @Autowired
    EvenementRepository eventRep;

    @Autowired
    LogistiqueRepository logisRep;

    @Override
    public Participant ajouterParticipant(Participant p) {
        log.info("=== Début de la méthode ajouterParticipant ===");
        log.info("Ajout d’un nouveau participant : Nom = {}, Prénom = {}, Tâche = {}", 
                 p.getNom(), p.getPrenom(), p.getTache());

        partRep.save(p);

        log.info("Participant ajouté avec succès dans la base de données.");
        log.info("=== Fin de la méthode ajouterParticipant ===");
        return p;
    }

    
    @Override
    @Scheduled(fixedRate = 60000)
    public void calculCout() {
        log.info("=== Début de la méthode calculCout ===");
        float cout = 0;
        List<Evenement> evenements = (List<Evenement>) eventRep.findAll();

        if (evenements.isEmpty()) {
            log.info("Aucun événement trouvé dans la base de données.");
        } else {
            log.info("Nombre d’événements trouvés : {}", evenements.size());
        }

        for (Evenement ev : evenements) {
            float coutEvent = logisRep.calculPrixLogistiquesReserves(true);
            cout += coutEvent;
            ev.setCout(cout);
            eventRep.save(ev);
            log.info("Mise à jour du coût pour l’événement (ID: {}, Description: {}). Nouveau coût: {}",
                     ev.getId(), ev.getDescription(), cout);
        }

        log.info("Coût total calculé pour tous les événements : {}", cout);
        log.info("=== Fin de la méthode calculCout ===");
    }


    @Override
    public List<Participant> getParReservLogis() {
        log.info("=== Début de la méthode getParReservLogis ===");
        log.info("Recherche des participants ayant une tâche = ORGANISATEUR et des logistiques réservées...");

        List<Participant> participants = partRep.participReservLogis(true, Tache.ORGANISATEUR);

        log.info("Nombre de participants organisateurs trouvés : {}", participants.size());
        log.info("=== Fin de la méthode getParReservLogis ===");
        return participants;
    }
}
