package app.YUP.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * This class represents the data transfer object (DTO) for an event response in GeoJSON format.
 * It is used to transfer data between processes or systems and can carry information for the event details along with its geographical coordinates.
 * The class is annotated with Lombok annotations to automatically generate boilerplate code like getters, setters, constructors, and builders.
 *
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseGeoJson {
    /**
     * The type of the GeoJSON object.
     */
    private String type;

    /**
     * The geographical coordinates of the event.
     */
    private Geometry geometry;

    /**
     * The properties of the event.
     */
    private EventProperties properties;

    /**
     * This class represents the Geometry of a GeoJSON object.
     * It contains the type of the geometry and the coordinates of the event.
     *
     * @version 1.0
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Geometry {
        /**
         * The type of the geometry.
         */
        private String type;

        /**
         * The coordinates of the event.
         * It is an array of doubles where the first element is the longitude and the second element is the latitude.
         */
        private double[] coordinates;
    }

    /**
     * This class represents the properties of an event in a GeoJSON object.
     * It contains all the details of the event.
     *
     * @version 1.0
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventProperties {
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
        private String tag;

        /**
         * The base64 encoded image of the event.
         */
        private String base64Image;

        /**
         * The maximum number of participants for the event.
         */
        private int participantsMaxNumber;

        /**
         * The current number of participants for the event.
         */
        private int participants;
    }
}