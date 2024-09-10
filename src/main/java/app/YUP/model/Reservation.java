package app.YUP.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "reservations", uniqueConstraints = {@UniqueConstraint(columnNames = {"event_id", "user_id"})})
public class Reservation {

    /**
     * The unique identifier of the reservation.
     * The @Id annotation specifies the primary key of the entity.
     * The @GeneratedValue annotation provides the generation strategy specification for the primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The event for which the reservation is made.
     * The @ManyToOne annotation indicates that this is a many-to-one relationship,
     * i.e., multiple reservations can be made for a single event.
     * The @JoinColumn annotation indicates that the 'event_id' field is a foreign key.
     */
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    /**
     * The user who made the reservation.
     * The @ManyToOne annotation indicates that this is a many-to-one relationship,
     * i.e., a single user can make multiple reservations.
     * The @JoinColumn annotation indicates that the 'user_id' field is a foreign key.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The unique prenotation code for the reservation.
     * The @Column annotation specifies that the prenotation code should be unique in the database.
     */
    @Column(unique = true)
    private String prenotationCode;

    /**
     * A boolean indicating whether the user was present at the event.
     */
    private boolean presence;

    /**
     * This method is automatically called after the object is initialized.
     * It sets the prenotation code to a unique value.
     * The @PrePersist annotation indicates that this method should be executed before the entity is persisted,
     * i.e., before it is saved to the database.
     */
    @PrePersist
    private void init() {
        this.prenotationCode = generateUniqueCode();
    }

    /**
     * This method generates a unique prenotation code for the reservation.
     *
     * @return a unique prenotation code.
     */
    private String generateUniqueCode() {
        return "YUP_RES-" + System.currentTimeMillis();
    }

    public boolean getPresence() {
        return presence;
    }
}