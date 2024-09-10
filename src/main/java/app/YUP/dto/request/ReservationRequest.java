package app.YUP.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * This class represents the data transfer object (DTO) for a reservation request.
 * It is used to transfer data between processes or systems and can carry information for the reservation details.
 * The class is annotated with Lombok annotations to automatically generate boilerplate code like getters, setters, constructors, and builders.
 *
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {

    /**
     * The unique identifier of the event for which the reservation is being made.
     * This is a UUID that uniquely identifies each event.
     */
    UUID eventId;

    /**
     * The unique identifier of the user making the reservation.
     * This is a UUID that uniquely identifies each user.
     */
    UUID userId;
}