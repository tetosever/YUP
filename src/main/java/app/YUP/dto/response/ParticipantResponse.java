package app.YUP.dto.response;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This class represents the data transfer object (DTO) for the event participant.
 * It is used to transfer data between processes or systems and can carry information for the event's participants.
 * The class is annotated with Lombok annotations to automatically generate boilerplate code like getters, setters, constructors, and builders.
 *
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ParticipantResponse {

    /**
     * The unique identifier of the reservation.
     */
    private UUID reservationId;

    /**
     * The unique identifier of the user.
     */
    private UUID userId;

    /**
     * The firstname of the user.
     */
    private String userFirstname;

    /**
     * The lastname of the user.
     */
    private String userLastname;

    /**
     * The email of the user.
     */
    private String userEmail;

    /**
     * The unique reservation code for the reservation.
     */
    private String prenotationCode;

    /**
     * A boolean indicating whether the user was present at the event.
     */
    private boolean presence;

}
