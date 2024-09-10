package app.YUP.model;

import java.util.Set;
import java.util.UUID;
import app.YUP.Enum.EventTag;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * This class represents an event in the system.
 * It contains information about the event, such as the name of the event, a description, and the time of the event.
 *
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "events")
public class Event {

    /**
     * The unique identifier of the event.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The user owner of the event.
     */
    @ManyToOne //(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner", referencedColumnName = "id")
    private User owner;

    /**
     * The name of the event.
     */
    @NotEmpty(message = "Event's name cannot be empty.")
    @Size(min = 5, max = 20, message = "Event's name cannot exceed 20 characters and must be longer than 5")
    private String name;

    /**
     * The description of the event with some important information.
     */
    @Size(max = 200, message = "Description cannot exceed 200 characters")
    private String description;

    /**
     * The location of the event will be held.
     */
    @NotEmpty(message = "Location cannot be empty")
    @Size(min = 5, max = 200, message = "Location cannot exceed 5 characters and must be longer than 200")
    private String location;

    /**
     * The start date and time of the event.
     */
    @NotNull(message = "Start date and time is required")
    private LocalDateTime startDateTime;

    /**
     * The end date and time of the event.
     */
    @NotNull(message = "End date and time is required")
    private LocalDateTime endDateTime;

    /**
     * The image representing the event.
     */
    @Column(name = "eventImage", columnDefinition = "bytea", length = 26214400)
    private byte[] eventImage;

    /**
     * The tag of the event which identify the category of the events for instance 'Aperitivo'
     */
    @Enumerated(EnumType.STRING)
    @NotNull(message = "event must have a tag")
    private EventTag tag;

    /**
     * The latitude of the event which identify the position of the event
     */
    @NotNull(message = "Latitude is required")
    private double latitude;

    /**
     * The longitude of the event which identify the position of the event
     */
    @NotNull(message = "Longitude is required")
    private double longitude;

    /**
     * Max number of participants that can join the event.
     */
    @Min(value = 2, message = "The event must have at least two participants.")
    @Max(value = 5000, message = "The event cannot have more than 5000 participants.")
    private int participantsMaxNumber;

    /**
     * Available number of participants that can join the event.
     */
    @Min(value = 0, message = " The number of reservations cannot go beyond 0.")
    private int participants;

    /**
     * This field represents the reservations associated with an event.
     * It is a Set of Reservation objects, meaning each event can have multiple reservations.
     * <p>
     * The @OneToMany annotation indicates that this is a one-to-many relationship,
     * i.e., one event can have multiple reservations.
     * <p>
     * The 'mappedBy = "event"' attribute indicates that the 'event' field in the Reservation entity
     * is the owner of the relationship (i.e., it contains the foreign key).
     * <p>
     * The 'cascade = CascadeType.REMOVE' attribute means that when an Event entity is deleted,
     * all its associated Reservation entities will also be deleted from the database.
     * <p>
     * The 'fetch = FetchType.LAZY' attribute means that the reservations are loaded only when they are explicitly requested,
     * improving performance.
     * <p>
     * The 'orphanRemoval = true' attribute means that when a Reservation entity is removed from this set,
     * it will also be deleted from the database.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Reservation> reservation;

    /**
     * Sets the initial number of participants to the maximum allowed.
     * This method is automatically called after the object is initialized.
     */
    @PrePersist
    private void init() {
        participants = participantsMaxNumber;
    }

    /**
     * Validates that the number of participants is less than or equal to the maximum allowed.
     * This method is used to ensure that the number of participants does not exceed the maximum allowed.
     *
     * @return true if the number of participants is less than or equal to the maximum allowed, false otherwise.
     */
    @AssertTrue(message = "The number of participants must be under participantsMaxNumber.")
    private boolean isParticipantsValid() {
        return participants <= participantsMaxNumber;
    }
}