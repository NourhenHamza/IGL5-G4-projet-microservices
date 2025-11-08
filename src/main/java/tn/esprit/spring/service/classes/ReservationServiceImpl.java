package tn.esprit.spring.service.classes;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.persistence.entities.Reservation;
import tn.esprit.spring.persistence.repositories.ReservationRepository;
import tn.esprit.spring.service.interfaces.IReservationService;

@Service
public class ReservationServiceImpl implements IReservationService {

    @Autowired
    ReservationRepository reservationRepository;

    @Override
    public List<Reservation> retrieveAllReservations() {
        List<Reservation> list = reservationRepository.findAll();
        System.out.println(" Nombre total de réservations récupérées : " + list.size());
        return list;
    }

    @Override
    public Reservation retrieveReservation(long idReservation) {
        return null;
    }

    @Override
     public Reservation retrieveReservation(Long idReservation) {
         Reservation r = reservationRepository.findById(idReservation).orElse(null);
         if (r != null) {
             System.out.println("🔍 Réservation trouvée : ID = " + r.getIdReservation());
         } else {
             System.out.println(" Aucune réservation trouvée avec l’ID " + idReservation);
         }
         return r;
     }


    @Override
    public Reservation addReservation(Reservation r) {
        Reservation saved = reservationRepository.save(r);
        System.out.println(" Nouvelle réservation ajoutée (ID = " + saved.getIdReservation() + ")");
        return saved;
    }

    @Override
    public Reservation updateReservation(Reservation r) {
        Reservation updated = reservationRepository.save(r);
        System.out.println(" Réservation mise à jour (ID = " + updated.getIdReservation() + ")");
        return updated;
    }



    @Override
    public void deleteReservation(Long idReservation) {
        reservationRepository.deleteById(idReservation);
        System.out.println(" Réservation supprimée (ID = " + idReservation + ")");
    }
}