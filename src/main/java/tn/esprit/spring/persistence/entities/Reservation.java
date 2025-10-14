package tn.esprit.spring.persistence.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idReservation;

    @Temporal(TemporalType.DATE)
    private Date dateReservation;

    private boolean confirme;

    @ManyToOne
    private Participant participant;

    @ManyToOne
    private Evenement evenement;
}
