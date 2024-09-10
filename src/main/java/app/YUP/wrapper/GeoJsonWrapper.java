package app.YUP.wrapper;

import app.YUP.dto.response.EventResponseGeoJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper class for GeoJson data.
 * This class is used to encapsulate the GeoJson data structure.
 * It contains a type and a list of features.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeoJsonWrapper {
    /**
     * The type of the GeoJson object.
     * This is usually set to "FeatureCollection" for a collection of GeoJson features.
     */
    private String type;

    /**
     * The list of features in the GeoJson object.
     * Each feature is represented by an instance of EventResponseGeoJson.
     */
    private List<EventResponseGeoJson> features;
}