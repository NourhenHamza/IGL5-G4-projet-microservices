package tn.esprit.spring.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.persistence.entities.Reservation;
import tn.esprit.spring.service.interfaces.IReservationService;

@RestController
@RequestMapping("/reservation")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    IReservationService reservationService;

    @GetMapping("/retrieve-all")
    public List<Reservation> getReservations() {
        return reservationService.retrieveAllReservations();
    }
/*
    @GetMapping("/retrieve/{id}")
    public Reservation retrieveReservation(@PathVariable("id") int idReservation) {
        return reservationService.retrieveReservation(idReservation);
    }*/
@GetMapping("/retrieve/{id}")
public ResponseEntity<?> retrieveReservation(@PathVariable("id") Long id) {
    System.out.println("🔍 Controller - Recherche reservation ID: " + id);

    try {
        Reservation reservation = reservationService.retrieveReservation(id);

        if (reservation != null) {
            System.out.println("✅ Controller - Reservation trouvée: " + reservation.getIdReservation());
            return ResponseEntity.ok(reservation);
        } else {
            System.out.println("❌ Controller - Aucune reservation avec ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"Reservation non trouvée avec ID: " + id + "\"}");
        }
    } catch (Exception e) {
        System.out.println("💥 Controller - Erreur: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"Erreur serveur: " + e.getMessage() + "\"}");
    }
}

    @PostMapping("/add")
    public Reservation addReservation(@RequestBody Reservation r) {
        return reservationService.addReservation(r);
    }

    @PutMapping("/update")
    public Reservation updateReservation(@RequestBody Reservation r) {
        return reservationService.updateReservation(r);
    }

    @DeleteMapping("/remove/{id}")
    public void removeReservation(@PathVariable("id") Long idReservation) {
        reservationService.deleteReservation(idReservation);
    }
}