package tn.esprit.spring.service.interfaces;

import java.util.Date;
import java.util.List;

import tn.esprit.spring.persistence.entities.Logistique;

public interface ILogistiqueService {
    Logistique ajoutAffectLogEven(Logistique l, String descriptionEvnm);
    List<Logistique> getLogistiquesDates(Date dateDebut, Date dateFin);
}