package tn.esprit.spring.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tn.esprit.spring.persistence.entities.Reservation;
import tn.esprit.spring.service.interfaces.IReservationService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
public class ReservationServiceImplTest {

    @Autowired
    private IReservationService reservationService;

    @Test
    @Order(1)
    public void testAddReservation() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date = df.parse("15/10/2025");

        Reservation r = new Reservation();
        r.setDateReservation(date);
        r.setConfirme(true);

        Reservation saved = reservationService.addReservation(r);

        assertNotEquals(0, saved.getIdReservation()); // idReservation != 0
        assertTrue(saved.isConfirme());
        log.info("Réservation ajoutée : " + saved);

        reservationService.deleteReservation(saved.getIdReservation());
    }

    @Test
    @Order(2)
    public void testRetrieveAllReservations() {
        List<Reservation> list = reservationService.retrieveAllReservations();
        assertNotNull(list);
        log.info("Nombre de réservations trouvées : " + list.size());
    }

    @Test
    @Order(3)
    public void testUpdateReservation() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date = df.parse("19/10/2025");

        Reservation r = new Reservation();
        r.setDateReservation(date);
        r.setConfirme(false); // initialement non confirmée

        Reservation saved = reservationService.addReservation(r);

        // Mise à jour
        saved.setConfirme(true); // maintenant confirmée
        Reservation updated = reservationService.updateReservation(saved);

        assertTrue(updated.isConfirme());
        log.info("🛠 Réservation mise à jour : " + updated);

        reservationService.deleteReservation(updated.getIdReservation());
    }

    @Test
    @Order(4)
    public void testDeleteReservation() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date date = df.parse("10/10/2025");

        Reservation r = new Reservation();
        r.setDateReservation(date);
        r.setConfirme(false);

        Reservation saved = reservationService.addReservation(r);

        reservationService.deleteReservation(saved.getIdReservation());
        log.info("Réservation supprimée avec succès !");
    }
}
