package app.YUP.service;

import app.YUP.dto.request.ReservationRequest;
import app.YUP.dto.response.EventResponse;
import app.YUP.exception.InvalidParameterException;
import app.YUP.exception.InvalidPermissionException;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.model.Event;
import app.YUP.model.Reservation;
import app.YUP.model.User;
import app.YUP.repository.ReservationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private UserService userService;

    @Mock
    private EventService eventService;

    @Mock
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReservation_HappyPath() {
        User user = new User();
        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setStartDateTime(LocalDateTime.now().plusDays(1));
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setUserId(UUID.randomUUID());
        reservationRequest.setEventId(event.getId());

        when(userService.getUserById(String.valueOf(any(UUID.class)))).thenReturn(user);
        when(eventService.getEventById(any(UUID.class))).thenReturn(event);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        reservationService.createReservation(reservationRequest, user);

        verify(eventService, times(1)).getEventById(any(UUID.class));
        verify(eventService, times(1)).updateParticipants(any(UUID.class), anyInt());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void createReservation_HappyPath_whenStartDateTimeIsAfterThanNow() {
        User user = new User();
        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setStartDateTime(LocalDateTime.now().minusDays(1));
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setUserId(UUID.randomUUID());
        reservationRequest.setEventId(event.getId());

        when(userService.getUserById(String.valueOf(any(UUID.class)))).thenReturn(user);
        when(eventService.getEventById(any(UUID.class))).thenReturn(event);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(new Reservation());

        Assertions.assertThrows(InvalidParameterException.class, () -> reservationService.createReservation(reservationRequest, user));
    }

    @Test
    void createReservation_WithNonExistingEvent() {
        User user = new User();
        ReservationRequest reservationRequest = new ReservationRequest();
        reservationRequest.setUserId(UUID.randomUUID());
        reservationRequest.setEventId(UUID.randomUUID());

        when(userService.getUserById(String.valueOf(any(UUID.class)))).thenReturn(user);
        when(eventService.getEventById(any(UUID.class))).thenReturn(null);

        Assertions.assertThrows(NullPointerException.class, () -> reservationService.createReservation(reservationRequest, user));

        verify(eventService, times(1)).getEventById(any(UUID.class));
        verify(eventService, times(0)).updateParticipants(any(UUID.class), anyInt());
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }

    @Test
    void checkIfEventIsReservedByUser_whenEventIsReservedByUser() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Event event = new Event();
        event.setId(eventId);
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        when(reservationRepository.findByUserId(user.getId())).thenReturn(reservations);

        assertTrue(reservationService.checkIfEventIsReservedByUser(user, eventId));
    }

    @Test
    void checkIfEventIsReservedByUser_whenEventIsNotReservedByUser() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Event event = new Event();
        event.setId(UUID.randomUUID());
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        when(reservationRepository.findByUserId(user.getId())).thenReturn(reservations);

        assertFalse(reservationService.checkIfEventIsReservedByUser(user, eventId));
    }

    @Test
    void checkIfEventIsReservedByUser_whenUserHasNoReservations() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());

        when(reservationRepository.findByUserId(user.getId())).thenReturn(new ArrayList<>());

        assertFalse(reservationService.checkIfEventIsReservedByUser(user, eventId));
    }

    @Test
    void getAllEventsReservedByUser_HappyPath() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Event event = new Event();
        event.setId(UUID.randomUUID());
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        when(userService.existsById(userId)).thenReturn(true);
        when(reservationRepository.findByUserId(userId)).thenReturn(reservations);
        when(eventService.mapEntityToDTO(any(Event.class))).thenReturn(new EventResponse());

        List<EventResponse> eventResponses = reservationService.getAllEventsReservedByUser(userId);

        verify(userService, times(1)).existsById(userId);
        verify(reservationRepository, times(1)).findByUserId(userId);
        verify(eventService, times(1)).mapEntityToDTO(any(Event.class));

        assertFalse(eventResponses.isEmpty());
    }

    @Test
    void getAllEventsReservedByUser_WhenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(userService.existsById(userId)).thenReturn(false);

        assertThrows(InvalidParameterException.class, () -> reservationService.getAllEventsReservedByUser(userId));

        verify(userService, times(1)).existsById(userId);
        verify(reservationRepository, times(0)).findByUserId(userId);
        verify(eventService, times(0)).mapEntityToDTO(any(Event.class));
    }

    @Test
    void getAllEventsReservedByUser_WhenUserHasNoReservations() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        when(userService.existsById(userId)).thenReturn(true);
        when(reservationRepository.findByUserId(userId)).thenReturn(new ArrayList<>());

        List<EventResponse> eventResponses = reservationService.getAllEventsReservedByUser(userId);

        verify(userService, times(1)).existsById(userId);
        verify(reservationRepository, times(1)).findByUserId(userId);
        verify(eventService, times(0)).mapEntityToDTO(any(Event.class));

        assertTrue(eventResponses.isEmpty());
    }

    @Test
    void updatePresenceByPrenotationCode_HappyPath() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Event event = new Event();
        event.setId(eventId);
        event.setOwner(user);
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setPrenotationCode("YUP_RES-123456");
        reservation.setPresence(false);

        when(reservationRepository.findByPrenotationCode("YUP_RES-123456")).thenReturn(reservation);

        int result = reservationService.updatePresenceByPrenotationCode("YUP_RES-123456", userId, eventId);

        verify(reservationRepository, times(1)).findByPrenotationCode("YUP_RES-123456");
        verify(reservationRepository, times(1)).save(reservation);

        assertEquals(0, result);
        assertTrue(reservation.getPresence());
    }

    @Test
    void returnReservationCode_HappyPath() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Event event = new Event();
        event.setId(eventId);
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setPrenotationCode("YUP_RES-123456");
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        when(reservationRepository.findByUserId(user.getId())).thenReturn(reservations);

        String prenotationCode = reservationService.returnReservationCode(user, eventId);

        verify(reservationRepository, times(1)).findByUserId(user.getId());

        assertEquals("YUP_RES-123456", prenotationCode);
    }

    @Test
    void returnReservationCode_WhenUserHasNoReservations() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());

        when(reservationRepository.findByUserId(user.getId())).thenReturn(new ArrayList<>());

        String prenotationCode = reservationService.returnReservationCode(user, eventId);

        verify(reservationRepository, times(1)).findByUserId(user.getId());

        assertNull(prenotationCode);
    }

    @Test
    void updatePresenceByPrenotationCode_WhenAlreadyPresent() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Event event = new Event();
        event.setId(eventId);
        event.setOwner(user);
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setPrenotationCode("YUP_RES-123456");
        reservation.setPresence(true);

        when(reservationRepository.findByPrenotationCode("YUP_RES-123456")).thenReturn(reservation);

        int result = reservationService.updatePresenceByPrenotationCode("YUP_RES-123456", userId, eventId);

        verify(reservationRepository, times(1)).findByPrenotationCode("YUP_RES-123456");
        verify(reservationRepository, times(0)).save(any(Reservation.class));

        assertEquals(-1, result);
    }

    @Test
    void updatePresenceByPrenotationCode_WhenReservationNotFound() {
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(reservationRepository.findByPrenotationCode("YUP_RES-123456")).thenReturn(null);

        int result = reservationService.updatePresenceByPrenotationCode("YUP_RES-123456", userId, eventId);

        verify(reservationRepository, times(1)).findByPrenotationCode("YUP_RES-123456");
        verify(reservationRepository, times(0)).save(any(Reservation.class));

        assertEquals(-2, result);
    }

    @Test
    void updatePresenceByPrenotationCode_WhenEventIdMismatch() {
        UUID eventId = UUID.randomUUID();
        UUID wrongEventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Event event = new Event();
        event.setId(eventId);
        event.setOwner(user);
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setPrenotationCode("YUP_RES-123456");
        reservation.setPresence(false);

        when(reservationRepository.findByPrenotationCode("YUP_RES-123456")).thenReturn(reservation);

        assertThrows(InvalidParameterException.class, () -> reservationService.updatePresenceByPrenotationCode("YUP_RES-123456", userId, wrongEventId));

        verify(reservationRepository, times(1)).findByPrenotationCode("YUP_RES-123456");
        verify(reservationRepository, times(0)).save(any(Reservation.class));
    }

    @Test
    void returnReservationCode_WhenReservationNotFound() {
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());

        when(reservationRepository.findByUserId(user.getId())).thenReturn(new ArrayList<>());

        String prenotationCode = reservationService.returnReservationCode(user, eventId);

        verify(reservationRepository, times(1)).findByUserId(user.getId());

        assertNull(prenotationCode);
    }

    @Test
    void returnReservationCode_WhenEventIdDoesNotMatch() {
        UUID eventId = UUID.randomUUID();
        UUID wrongEventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Event event = new Event();
        event.setId(wrongEventId);
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setPrenotationCode("YUP_RES-123456");
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation);

        when(reservationRepository.findByUserId(user.getId())).thenReturn(reservations);

        String prenotationCode = reservationService.returnReservationCode(user, eventId);

        verify(reservationRepository, times(1)).findByUserId(user.getId());

        assertNull(prenotationCode);
    }

    @Test
    public void deleteReservation_WithExistingReservation_RemovesReservation() {
        Event event = Event.builder().id(UUID.randomUUID()).build();
        User user = User.builder().id(UUID.randomUUID()).build();
        Reservation reservation = Reservation.builder().user(user).event(event).build();

        when(reservationRepository.findByUserIdAndEventId(user.getId(), event.getId())).thenReturn(Optional.ofNullable(reservation));
        when(eventService.getEventById(event.getId())).thenReturn(new Event());

        reservationService.deleteReservation(event, user);

        verify(reservationRepository, times(1)).delete(any());
    }

    @Test
    public void deleteReservation_WithNonExistingReservation_ThrowsException() {
        Event event = Event.builder().id(UUID.randomUUID()).build();
        User user = User.builder().id(UUID.randomUUID()).build();

        when(reservationRepository.findByUserIdAndEventId(user.getId(), event.getId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservationService.deleteReservation(event, user));
    }

    @Test
    void checkIfUserIsOwnerOfReservation_WhenUserIsOwnerOfReservation_ThrowsInvalidParameterException() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Event event = new Event();
        event.setOwner(user);

        assertThrows(InvalidParameterException.class, () -> reservationService.checkIfUserIsOwnerOfReservation(userId, user, event));
    }

    @Test
    void checkIfUserIsOwnerOfReservation_WhenUserIsNotOwnerOfReservation_DoesNotThrowException() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Event event = new Event();
        event.setOwner(User.builder().id(UUID.randomUUID()).build());

        assertDoesNotThrow(() -> reservationService.checkIfUserIsOwnerOfReservation(userId, user, event));
    }

    @Test
    void checkIfUserIsOwnerOfReservation_WhenUserTriesToCreateReservationForAnotherUser_ThrowsInvalidPermissionException() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        Event event = new Event();
        event.setOwner(new User());

        assertThrows(InvalidPermissionException.class, () -> reservationService.checkIfUserIsOwnerOfReservation(userId, user, event));
    }

    @Test
    void checkIfUserIsOwnerOfReservation_WhenUserTriesToCreateReservationForSelf_DoesNotThrowException() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        Event event = new Event();
        event.setOwner(User.builder().id(UUID.randomUUID()).build());

        assertDoesNotThrow(() -> reservationService.checkIfUserIsOwnerOfReservation(userId, user, event));
    }

}