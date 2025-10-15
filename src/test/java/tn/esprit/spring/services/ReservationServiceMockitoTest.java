package tn.esprit.spring.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.persistence.entities.Reservation;
import tn.esprit.spring.persistence.repositories.ReservationRepository;
import tn.esprit.spring.services.ReservationServiceImpl;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceMockitoTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void testAddReservation() {
        // Création de la réservation
        Reservation r = new Reservation();
        r.setIdReservation(1);
        r.setDateReservation(new Date());
        r.setConfirme(true);

        when(reservationRepository.save(r)).thenReturn(r);

        Reservation saved = reservationService.addReservation(r);
        assertTrue(saved.isConfirme());
        verify(reservationRepository, times(1)).save(r);
    }

    @Test
    void testRetrieveAllReservations() {
        List<Reservation> mockList = Arrays.asList(
                new Reservation(1, new Date(), true, null, null)
        );
        when(reservationRepository.findAll()).thenReturn(mockList);

        List<Reservation> result = reservationService.retrieveAllReservations();
        assertEquals(1, result.size());
        verify(reservationRepository, times(1)).findAll();
    }

    @Test
    void testUpdateReservation() {
        Reservation r = new Reservation();
        r.setIdReservation(2);
        r.setDateReservation(new Date());
        r.setConfirme(false);

        when(reservationRepository.save(r)).thenReturn(r);

        Reservation updated = reservationService.updateReservation(r);
        assertFalse(updated.isConfirme());
        verify(reservationRepository, times(1)).save(r);
    }

    @Test
    void testDeleteReservation() {
        int id = 1;
        reservationService.deleteReservation(id);
        verify(reservationRepository, times(1)).deleteById(id);
    }
}
