package tn.esprit.spring.service.classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.persistence.entities.Evenement;
import tn.esprit.spring.persistence.entities.Logistique;
import tn.esprit.spring.persistence.repositories.EvenementRepository;
import tn.esprit.spring.persistence.repositories.LogistiqueRepository;
import tn.esprit.spring.service.interfaces.ILogistiqueService;

@Service
@Slf4j
@RequiredArgsConstructor
public class LogistiqueServiceImpl implements ILogistiqueService {

    private final EvenementRepository evenRep;
    private final LogistiqueRepository logistiqueRepository;

    @Override
    public Logistique ajoutAffectLogEven(Logistique l, String descriptionEvnm) {
        Evenement e = evenRep.findByDescription(descriptionEvnm);
        List<Logistique> logis;
        if (e.getLogistiques() == null) {
            logis = new ArrayList<>();
        } else {
            logis = e.getLogistiques();
        }
        logistiqueRepository.save(l);
        logis.add(l);
        e.setLogistiques(logis);
        evenRep.save(e);
        return l;
    }

    @Override
    public List<Logistique> getLogistiquesDates(Date dateDebut, Date dateFin) {
        List<Evenement> events = evenRep.findByDatedBetween(dateDebut, dateFin);
        List<Logistique> allLogists = new ArrayList<>();
        for (Evenement e : events) {
            for (Logistique l : e.getLogistiques()) {
                if (l.isReserve()) {
                    allLogists.add(l);
                }
            }
        }
        return allLogists;
    }
}