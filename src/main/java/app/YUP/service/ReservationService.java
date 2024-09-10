package app.YUP.service;

import app.YUP.dto.request.ReservationRequest;
import app.YUP.dto.response.EventResponse;
import app.YUP.dto.response.ParticipantResponse;
import app.YUP.exception.InvalidParameterException;
import app.YUP.exception.InvalidPermissionException;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.model.Event;
import app.YUP.model.Reservation;
import app.YUP.model.User;
import app.YUP.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final UserService userService;
    private final EventService eventService;
    private final ReservationRepository reservationRepository;

    /**
     * Creates a new reservation in the system.
     *
     * @param reservationRequest the reservation dto to create
     */
    @Transactional
    public Reservation createReservation(ReservationRequest reservationRequest, User user) {
        Event event = eventService.getEventById(reservationRequest.getEventId());

        if (!checkUserCanReserveBeforeStartDateTime(event)) {
            throw new InvalidParameterException("Event has already started");
        }

        eventService.updateParticipants(event.getId(), event.getParticipants() - 1);

        Reservation reservation = Reservation.builder().user(user).event(event).build();
        return reservationRepository.save(reservation);
    }

    /**
     * This method checks if the current time is before the start time of the event.
     * It is used to ensure that a user can only reserve an event before it starts.
     *
     * @param event The Event object for which the start time is being checked.
     * @return true if the current time is before the start time of the event, false otherwise.
     */
    public boolean checkUserCanReserveBeforeStartDateTime(Event event) {
        return LocalDateTime.now().isBefore(event.getStartDateTime());
    }

    /**
     * Deletes a reservation from the system.
     *
     * @param event The event for which the reservation should be deleted.
     * @param user    The user who made the reservation.
     *
     * @throws ResourceNotFoundException If no reservation exists for the given eventId and user.
     * @throws InvalidParameterException If the eventId is null or the user is null.
     */
    @Transactional
    public void deleteReservation(Event event, User user) {
        Reservation reservation = getByUserIdAndEventId(user.getId(), event.getId());

        eventService.updateParticipants(event.getId(), event.getParticipants() + 1);
        reservationRepository.delete(reservation);
    }

    /**
     * Retrieves a reservation by user ID and event ID.
     *
     * @param userId The UUID of the user who made the reservation.
     * @param eventId The UUID of the event for which the reservation was made.
     * @return The reservation that matches the provided user ID and event ID.
     * @throws ResourceNotFoundException If no reservation exists for the given user ID and event ID.
     */
    public Reservation getByUserIdAndEventId(UUID userId, UUID eventId) {
        Optional<Reservation> reservationOptional = reservationRepository.findByUserIdAndEventId(userId, eventId);

        if (reservationOptional.isEmpty()) {
            throw new ResourceNotFoundException("Reservation does not exist");
        }

        return reservationOptional.get();
    }

    /**
     * Checks if a specific event is reserved by a user.
     *
     * @param user    The user to check the reservations for.
     * @param eventId The ID of the event to check.
     * @return true if the user has reserved the event, false otherwise.
     */
    public boolean checkIfEventIsReservedByUser(User user, UUID eventId) {
        boolean isReserved = false;
        List<Reservation> reservations = reservationRepository.findByUserId(user.getId());
        for (Reservation reservation : reservations) {
            if (reservation.getEvent().getId().equals(eventId)) {
                isReserved = true;
                break;
            }
        }
        return isReserved;
    }

    /**
     * This method is used to return the reservation code for a specific event that a user has reserved.
     * It first fetches all the reservations made by the user.
     * Then it iterates over these reservations to find the one that matches the provided event ID.
     * If it finds a match, it breaks the loop and returns the prenotation code of the reservation.
     * If it doesn't find a match, it throws an InvalidParameterException indicating that the user has not reserved this event.
     *
     * @param user    The User object for whom to fetch the reservation code.
     * @param eventId The UUID of the event for which to fetch the reservation code.
     * @return The reservation code as a String.
     * @throws InvalidParameterException if the user has not reserved the event.
     */
    public String returnReservationCode(User user, UUID eventId) {
        Reservation reservation = null;
        List<Reservation> reservations = reservationRepository.findByUserId(user.getId());
        for (Reservation res : reservations) {
            if (res.getEvent().getId().equals(eventId)) {
                reservation = res;
                break;
            }
        }
        if (reservation != null) {
            return reservation.getPrenotationCode();
        } else {
            return null;
        }
    }

    /**
     * Retrieves all events reserved by a specific user.
     * <p>
     * This method first checks if a user with the provided UUID exists in the system.
     * If the user does not exist, it throws an InvalidParameterException.
     * If the user exists, it fetches all reservations made by the user.
     * If the user has not made any reservations, it throws an InvalidParameterException.
     * If the user has made reservations, it maps each reservation to its corresponding event and returns a list of these events.
     *
     * @param userId The UUID of the user for whom to fetch reserved events.
     * @return A list of Event objects that were reserved by the user with the given UUID.
     * @throws InvalidParameterException if the user does not exist or if the user has not made any reservations.
     */
    public List<EventResponse> getAllEventsReservedByUser(UUID userId) {
        boolean userExist = userService.existsById(userId);
        if (!userExist) {
            throw new InvalidParameterException("User does not exist");
        } else {
            List<Reservation> reservations = reservationRepository.findByUserId(userId);
            if (reservations.isEmpty()) {
                return new ArrayList<EventResponse>();
            } else {
                List<Event> tempEvent = reservations.stream()
                        .map(Reservation::getEvent)
                        .collect(Collectors.toList());

                return tempEvent.stream()
                        .map(eventService::mapEntityToDTO)
                        .collect(Collectors.toList());
            }
        }
    }

    /**
     * Updates the presence status of a reservation by its prenotation code.
     *
     * @param prenotationCode The unique identifier for the reservation.
     * @param userID          The ID of the user who is updating the reservation.
     * @param eventID         The ID of the event associated with the reservation.
     * @return -2 if the reservation with the given prenotation code does not exist,
     * -1 if the reservation is already marked as present,
     * 0 if the presence status of the reservation is successfully updated.
     * @throws InvalidParameterException if the event ID does not match the event associated with the reservation.
     * @throws InvalidPermissionException if the user does not have permission to update the reservation for the given event.
     */
    public int updatePresenceByPrenotationCode(String prenotationCode, UUID userID, UUID eventID) {
        Reservation reservation = reservationRepository.findByPrenotationCode(prenotationCode);
        if (reservation == null) {
            return -2;
        } else if (!reservation.getEvent().getId().equals(eventID)) {
            throw new InvalidParameterException("Exception associated with the wrong event");
        } else if (!reservation.getEvent().getOwner().getId().equals(userID)) {
            throw new InvalidPermissionException("You do not have permission to scan reservations for this event");
        } else if (reservation.getPresence()) {
            return -1;
        } else {
            reservation.setPresence(true);
            reservationRepository.save(reservation);
            return 0;
        }
    }

    /**
     * This method checks if the logged-in user is the owner of the reservation.
     * It throws an exception if the logged-in user is trying to create a reservation for another user or if the user is trying to create a reservation for an event they own.
     *
     * @param userId The UUID of the user for whom the reservation is being created.
     * @param userLogged The User object of the logged-in user.
     * @param event The Event object for which the reservation is being created.
     *
     * @throws InvalidPermissionException if the logged-in user is trying to create a reservation for another user.
     * @throws InvalidParameterException if the user is trying to create a reservation for an event they own.
     */
    public void checkIfUserIsOwnerOfReservation(UUID userId, User userLogged, Event event) {
        if (!userLogged.getId().equals(userId)){
            throw new InvalidPermissionException("You don't have permission to create a reservation for another user" + userLogged.getId() + " " + userId);
        }
        if (event.getOwner().getId().equals(userId)) {
            throw new InvalidParameterException("You can't create a reservation for an event that you own");
        }
    }

    /**
     * Retrieves the reservations of a given event in alphabetical order by participant last name and first name
     *
     * This method takes an Event UUID as input and returns a set of ParticipantResponse
     * objects representing the reservations for the event. If the event has no reservations,
     * an empty set is returned.
     *
     * @param eventId the UUID of the event for which to retrieve the reservations
     * @return a set of ParticipantResponse objects representing the reservations of the event,
     *         or an empty set if there are no reservations
     * @throws IllegalArgumentException if the event does not exist
     */
    public Set<ParticipantResponse> getAllReservationsForEvent(UUID eventId) {
        boolean eventExist = eventService.existsById(eventId);
        if (!eventExist) {
            throw new java.security.InvalidParameterException("Event does not exist");
        }

        List<Reservation> reservations = reservationRepository.findByEventId(eventId);
        if (reservations.isEmpty()) {
            return Collections.emptySet();
        }

        return reservations.stream()
                .map(this::convertToParticipantResponse)
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator
                        .comparing((ParticipantResponse p) -> p.getUserLastname().toLowerCase())
                        .thenComparing((ParticipantResponse p) -> p.getUserFirstname().toLowerCase()))));


    }

    /**
     * Counts the number of participants who are present and not present.
     *
     * @param participants a set of {@link ParticipantResponse} objects representing the participants of an event
     * @return a map with two keys:
     *         <ul>
     *           <li><strong>"present"</strong> - the count of participants with presence set to true</li>
     *           <li><strong>"notPresent"</strong> - the count of participants with presence set to false</li>
     *         </ul>
     */
    public Map<String, Integer> getPresenceCounts(Set<ParticipantResponse> participants) {
        int presenceTrueCount = 0;
        int presenceFalseCount = 0;

        for (ParticipantResponse participant : participants) {
            if (participant.isPresence()) {
                presenceTrueCount++;
            } else {
                presenceFalseCount++;
            }
        }

        Map<String, Integer> presenceCounts = new HashMap<>();
        presenceCounts.put("present", presenceTrueCount);
        presenceCounts.put("notPresent", presenceFalseCount);

        return presenceCounts;
    }



    /**
     * Converts a Reservation object to a ParticipantResponse object.
     *
     * This method extracts relevant information from a Reservation and its associated User
     * to create a ParticipantResponse object.
     *
     * @param reservation the Reservation object to convert
     * @return a ParticipantResponse object containing the reservation details
     */
    private ParticipantResponse convertToParticipantResponse(Reservation reservation) {
        User user = reservation.getUser();
        return ParticipantResponse.builder()
                .reservationId(reservation.getId())
                .userId(user.getId())
                .userFirstname(user.getFirstname())
                .userLastname(user.getLastname())
                .userEmail(user.getEmail())
                .prenotationCode(reservation.getPrenotationCode())
                .presence(reservation.getPresence())
                .build();
    }
}
