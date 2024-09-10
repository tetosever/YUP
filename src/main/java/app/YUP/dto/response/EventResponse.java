package app.YUP.dto.response;

import app.YUP.Enum.EventTag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This class represents the data transfer object (DTO) for an event response.
 * It is used to transfer data between processes or systems and can carry information for the event details.
 * The class is annotated with Lombok annotations to automatically generate boilerplate code like getters, setters, constructors, and builders.
 *
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventResponse {

        /**
         * The unique identifier of the event.
         * This is a UUID that uniquely identifies each event.
         */
        private UUID id;

        /**
         * The name of the event.
         */
        private String name;

        /**
         * The description of the event.
         */
        private String description;

        /**
         * The location of the event.
         */
        private String location;

        /**
         * The start date and time of the event.
         */
        private LocalDateTime startDateTime;

        /**
         * The end date and time of the event.
         */
        private LocalDateTime endDateTime;

        /**
         * The image of the event.
         */
        private byte[] eventImage;

        /**
         * The tag of the event.
         */
        private EventTag tag;

        /**
         * The maximum number of participants for the event.
         */
        private int participantsMaxNumber;

        /**
         * The current number of participants for the event.
         */
        private int participants;
}