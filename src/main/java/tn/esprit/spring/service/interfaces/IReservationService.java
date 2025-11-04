package tn.esprit.spring.service.interfaces;

import java.util.List;
import tn.esprit.spring.persistence.entities.Reservation;

public interface IReservationService {
    List<Reservation> retrieveAllReservations();
    Reservation retrieveReservation(int idReservation);
    Reservation addReservation(Reservation r);
    Reservation updateReservation(Reservation r);
    void deleteReservation(int idReservation);
}