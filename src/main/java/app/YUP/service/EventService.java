package app.YUP.service;

import app.YUP.Enum.EventTag;
import app.YUP.dto.request.EventRequest;
import app.YUP.dto.response.EventResponse;
import app.YUP.dto.response.EventResponseGeoJson;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.exception.UnauthorizedAccessException;
import app.YUP.model.Event;
import app.YUP.model.Reservation;
import app.YUP.model.User;
import app.YUP.repository.EventRepository;
import app.YUP.repository.ReservationRepository;
import app.YUP.wrapper.GeoJsonWrapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents the service layer for the Event entity.
 * It contains methods for creating, retrieving, validating, and converting events.
 * The class is annotated with Spring's @Service annotation to indicate that it's a service component.
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final UserService userService;

    /**
     * Creates a new event by saving the provided event object to the repository.
     *
     * @param event          The event object to be created and saved.
     * @param userId       The username of the user creating the event.
     * @param result         The BindingResult object that holds the result of the validation of the event.
     * @param eventImageFile The image file of the event.
     * @param model          The Model object that holds the model attributes.
     */
    public void createEvent(EventRequest event, String userId, BindingResult result, MultipartFile eventImageFile, Model model) {
        validateEvent(event, result, eventImageFile, model);
        Event eventToSave = mapDTOToEntity(event, userId);
        eventRepository.save(eventToSave);
    }

    /**
     * Service method to delete a user event from the repository.
     *
     * @param event the {@link Event} object to be deleted. It must not be {@code null}.
     * @throws ResourceNotFoundException if the user to delete is null.
     */
    @Transactional
    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }

    /**
     * Retrieves all events owned by a specific user.
     *
     * @param userId The username of the user.
     * @return A list of EventResponse objects representing the events owned by the user.
     */
    public List<EventResponse> getEventsByOwner(String userId) {
        List<EventResponse> eventResponses = new ArrayList<>();
        User owner = userService.getUserById(userId);
        List<Event> events = eventRepository.findByOwner(owner);

        for (Event event : events) {
            eventResponses.add(mapEntityToDTO(event));
        }

        return eventResponses;
    }

    /**
     * This method checks if a user is the owner of a specific event.
     * It takes the userId of the user and the eventId of the event as parameters.
     * It retrieves the user and event using their respective services and compares the owner of the event with the user.
     * If the user is not the owner of the event, it throws an UnauthorizedAccessException.
     *
     * @param userId  This is the ID of the user to be checked.
     * @param eventId This is the UUID of the event to be checked.
     * @throws UnauthorizedAccessException if the user is not the owner of the event.
     */
    @Transactional
    public void isEventOwner( String userId, UUID eventId) {
        User owner = userService.getUserById(userId);
        Event event = getEventById(eventId);

        if (!event.getOwner().equals(owner)) {
            throw new UnauthorizedAccessException("User is not the owner of the event");
        }
    }


    /**
     * Validates the event request.
     *
     * @param event          The event request to validate.
     * @param result         The BindingResult object that holds the result of the validation.
     * @param eventImageFile The image file of the event.
     * @param model          The Model object that holds the model attributes.
     */
    private void validateEvent(EventRequest event, BindingResult result, MultipartFile eventImageFile, Model model) {
        model.addAttribute("tags", EventTag.values());

        //Check if the fields are valid according to the policy defined in the Event class (es. notEmpty and size)
        if (result.hasErrors()) {
            throw new InvalidParameterException("Some filed of the form are not valid");
        }

        // Check if a image file is uploaded and valid
        try {
            if (eventImageFile != null && !eventImageFile.isEmpty()) {
                if (!isImageFormat(eventImageFile.getContentType())) {
                    result.rejectValue("eventImageFile", "eventImageFile.invalid", "Uploaded file is not an image (use JPG/JPEG or PNG");
                    throw new InvalidParameterException("Uploaded file is not an image (use JPG/JPEG or PNG");
                }
                event.setEventImage(eventImageFile.getBytes());
            }
        } catch (IOException e) {

            result.rejectValue("eventImageFile", "eventImageFile.invalid", "Image file is empty");
            throw new InvalidParameterException("Image file is empty");
        }

        // Date validation to verify that they are compliant
        LocalDateTime startDate = event.getStartDateTime();
        LocalDateTime endDate = event.getEndDateTime();
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (startDate != null && startDate.isBefore(currentDateTime)) {
            result.rejectValue("startDateTime", "startDateTime.invalid", "Start date must be in the future");
            throw new InvalidParameterException("Start date must be in the future");
        }

        if (startDate != null && startDate.isAfter(endDate)) {
            result.rejectValue("endDateTime", "invalid", "End date must be after start date");
            throw new InvalidParameterException("End date must be after start dat");
        }
    }

    /**
     * Checks if the provided content type represents an image format.
     *
     * @param contentType The content type to be checked.
     * @return {@code true} if the content type represents an image format, {@code false} otherwise.
     */
    private boolean isImageFormat(String contentType) {
        return contentType != null && (contentType.startsWith("image/jpeg") || contentType.startsWith("image/png") || contentType.startsWith("image/jpg"));
    }

    /**
     * Retrieves an event image by their ID (event id).
     *
     * @param id The id of the event to retrieve.
     * @return The blob of the event image
     */
    public byte[] getImageData(UUID id) {
        Event event = eventRepository.findById(id).orElse(null);
        if (event != null) {
            return event.getEventImage(); // Assuming getEventImage() returns the BLOB image data
        } else {
            return null;
        }
    }

    /**
     * Retrieves all events from the event repository and maps them to their corresponding DTOs.
     *
     * @return A list of {@link EventResponse} containing all events mapped to their DTO representation.
     */
    public List<EventResponse> getAllEvents() {
        List<EventResponse> eventResponses = new ArrayList<>();
        List<Event> events = eventRepository.findAll();

        for (Event event : events) {
            if (LocalDateTime.now().isBefore(event.getEndDateTime())) {
                eventResponses.add(mapEntityToDTO(event));
            }
        }

        return eventResponses;
    }

    /**
     * Retrieves all events from the event repository and maps them to their corresponding DTOs.
     *
     * @return A list of {@link GeoJsonWrapper} containing all events mapped to their DTO representation.
     */
    public GeoJsonWrapper getAllGEOEvents() {
        List<Event> eventsFiltered = new ArrayList<>();
        List<Event> events = eventRepository.findAll();

        for (Event event : events) {
            if (LocalDateTime.now().isBefore(event.getEndDateTime())) {
                eventsFiltered.add(event);
            }
        }

        return convertToGeoJson(eventsFiltered);
    }

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param eventId The UUID identifying the event to retrieve.
     * @return The event corresponding to the provided ID.
     * @throws ResourceNotFoundException if no event with the specified ID is found.
     */
    public Event getEventById(UUID eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    /**
     * Retrieves the participants of a given event.
     *
     * This method takes an Event UUID as input and returns a set of Users who have
     * reservations for the event. If the event has no reservations, an empty set is returned.
     *
     * @param eventId the Event object from which to retrieve the participants
     * @return a set of User objects who are participants of the event, or an empty set if there are no reservations
     */
    @Transactional
    public Set<User> getEventParticipants(UUID eventId) {
        boolean eventExist = existsById(eventId);
        if (!eventExist) {
            throw new InvalidParameterException("Event does not exist");
        }

        List<Reservation> reservations = reservationRepository.findByEventId(eventId);
        if (reservations.isEmpty()) {
            return new HashSet<>();
        }

        return reservations.stream()
                .map(Reservation::getUser)
                .collect(Collectors.toSet());
    }


    /**
     * Retrieves the emails of participants of a given event.
     *
     * This method takes an Event object as input and returns a set of emails of users who have
     * reservations for the event. If the event has no reservations, an empty set is returned.
     *
     * @param participants the Event object from which to retrieve the participants' emails
     * @return a set of emails of users who are participants of the event, or an empty set if there are no reservations
     */
    public Set<String> getEventParticipantsEmails(Set<User> participants) {
        if (participants == null ||participants.isEmpty()) {
            return new HashSet<>();
        }

        return participants.stream()
                .map(User::getEmail)
                .collect(Collectors.toSet());
    }

    /**
     * Maps an EventDTO object to an Event entity object.
     *
     * @param eventRequest The EventDTO object to be mapped.
     * @param userId     The username of the user creating the event.
     * @return The Event entity object mapped from the EventDTO object.
     */
    private Event mapDTOToEntity(EventRequest eventRequest, String userId) {
        return Event.builder()
                .name(eventRequest.getName())
                .description(eventRequest.getDescription())
                .location(eventRequest.getLocation())
                .startDateTime(eventRequest.getStartDateTime())
                .endDateTime(eventRequest.getEndDateTime())
                .eventImage(eventRequest.getEventImage())
                .owner(userService.getUserById(userId))
                .tag(eventRequest.getTag())
                .latitude(Double.parseDouble(eventRequest.getLatitude()))
                .longitude(Double.parseDouble(eventRequest.getLongitude()))
                .participantsMaxNumber(eventRequest.getParticipantsMaxNumber())
                .build();
    }

    /**
     * Maps an EventDTO object to an Event entity object.
     *
     * @param event The Event object used for mapping and creation of EventResponse object.
     * @return The EventResponse DTO object mapped from the Event object.
     */
    public EventResponse mapEntityToDTO(Event event) {
        EventResponse eventResponse = EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .location(event.getLocation())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .tag(event.getTag())
                .participantsMaxNumber(event.getParticipantsMaxNumber())
                .participants(event.getParticipants())
                .build();

        if (event.getEventImage() != null) {
            eventResponse.setEventImage(event.getEventImage());
        }

        return eventResponse;
    }

    /**
     * Converts a list of events to a GeoJSON representation.
     * Each event is converted to a GeoJSON feature.
     *
     * @param events The list of events to convert.
     * @return A GeoJsonWrapper containing the GeoJSON representation of the events.
     */
    public GeoJsonWrapper convertToGeoJson(List<Event> events) {
        List<EventResponseGeoJson> eventGeoJsonList = new ArrayList<>();
        for (Event event : events) {
            EventResponseGeoJson geoJson = convertToEventResponseGeoJson(event);
            eventGeoJsonList.add(geoJson);
        }

        GeoJsonWrapper wrapper = new GeoJsonWrapper();
        wrapper.setType("FeatureCollection");
        wrapper.setFeatures(eventGeoJsonList);

        return wrapper;
    }

    /**
     * Converts an Event object to a GeoJSON representation.
     *
     * @param event The event to convert.
     * @return An EventResponseGeoJson object representing the GeoJSON representation of the event.
     */
    private EventResponseGeoJson convertToEventResponseGeoJson(Event event) {
        EventResponseGeoJson.Geometry geometry = EventResponseGeoJson.Geometry.builder().type("Point").coordinates(new double[]{event.getLongitude(), event.getLatitude()}).build();
        EventResponseGeoJson.EventProperties properties;
        if (event.getEventImage() != null) {
            properties = EventResponseGeoJson.EventProperties.builder().name(event.getName()).description(event.getDescription()).tag(event.getTag().toString()).id(event.getId()).startDateTime(event.getStartDateTime()).endDateTime((event.getEndDateTime())).base64Image(Base64.getEncoder().encodeToString(event.getEventImage())).location(event.getLocation()).participantsMaxNumber(event.getParticipantsMaxNumber()).participants(event.getParticipants()).build();
        } else {
            properties = EventResponseGeoJson.EventProperties.builder().name(event.getName()).description(event.getDescription()).tag(event.getTag().toString()).id(event.getId()).startDateTime(event.getStartDateTime()).endDateTime((event.getEndDateTime())).base64Image("").location(event.getLocation()).participantsMaxNumber(event.getParticipantsMaxNumber()).participants(event.getParticipants()).build();
        }
        return EventResponseGeoJson.builder().type("Feature").geometry(geometry).properties(properties).build();
    }

    /**
     * Updates the number of participants for a specific event.
     *
     * @param eventId      The UUID identifying the event to update.
     * @param participants The new number of participants.
     */
    public void updateParticipants(UUID eventId, int participants) {
        if (participants >= 0) {
            eventRepository.updateParticipantsById(eventId, participants);
        } else {
            throw new InvalidParameterException("Participants number cannot be negative");
        }
    }

    public boolean existsById(UUID eventId) {
        return eventRepository.existsById(eventId);
    }
}
