package tn.esprit.spring.service.interfaces;

import java.util.List;
import tn.esprit.spring.persistence.entities.Reservation;

public interface IReservationService {
    List<Reservation> retrieveAllReservations();
    Reservation retrieveReservation(long idReservation);

    /* @Override
     public Reservation retrieveReservation(int idReservation) {
         Reservation r = reservationRepository.findById(idReservation).orElse(null);
         if (r != null) {
             System.out.println("🔍 Réservation trouvée : ID = " + r.getIdReservation());
         } else {
             System.out.println(" Aucune réservation trouvée avec l’ID " + idReservation);
         }
         return r;
     }
 */
    Reservation retrieveReservation(Long idReservation);

    Reservation addReservation(Reservation r);
    Reservation updateReservation(Reservation r);

    void deleteReservation(Long idReservation);
}