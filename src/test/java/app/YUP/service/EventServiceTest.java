package app.YUP.service;

import app.YUP.Enum.EventTag;
import app.YUP.Enum.Role;
import app.YUP.dto.request.EventRequest;
import app.YUP.dto.response.EventResponse;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.model.Event;
import app.YUP.model.User;
import app.YUP.repository.EventRepository;
import app.YUP.repository.ReservationRepository;
import app.YUP.wrapper.GeoJsonWrapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private UserService userService;
    @InjectMocks
    private EventService eventService;
    private HttpServletRequest mockRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockRequest = mock(HttpServletRequest.class);

    }


    @Test
    void getImageData_EventDoesNotExist_ReturnsNull() {
        UUID eventId = UUID.randomUUID();
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        byte[] result = eventService.getImageData(eventId);

        assertNull(result);
    }

    @Test
    void createEvent_Success() {
        HttpServletRequest request = new MockHttpServletRequest();
        ResponseCookie jwtCookie = ResponseCookie.from("JWT", "test-jwt-token").path("/").httpOnly(true).secure(true).build();
        request.setAttribute("JWT", jwtCookie);

        User user = User.builder().id(UUID.randomUUID()).username("mrossi").firstname("Mario").lastname("Rossi").email("mariorossi@example.com").role(Role.LOGGED_USER).build();

        EventRequest eventRequest = EventRequest.builder().name("Sample Event").description("This is a sample event description").location("Sample Location").startDateTime(LocalDateTime.now().plusHours(1)).endDateTime(LocalDateTime.now().plusHours(2)).latitude("51.5074").longitude("-0.1278").participantsMaxNumber(10).build();

        Event savedEvent = Event.builder().name(eventRequest.getName()).description(eventRequest.getDescription()).location(eventRequest.getLocation()).startDateTime(eventRequest.getStartDateTime()).endDateTime(eventRequest.getEndDateTime()).owner(user).build();

        BindingResult result = mock(BindingResult.class);
        MockMultipartFile eventImageFile = new MockMultipartFile("eventImageFile", new byte[]{});
        Model model = mock(Model.class);

        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);
        when(userService.getUserById(String.valueOf(user.getId()))).thenReturn(user);

        eventService.createEvent(eventRequest, String.valueOf(user.getId()), result, eventImageFile, model);

        verify(eventRepository).save(any(Event.class));
        verify(userService).getUserById(String.valueOf(user.getId()));
    }

    @Test
    void testGetEventsByOwner() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testUser");
        List<Event> events = new ArrayList<>();
        Event event = new Event();
        event.setName("Test Event");
        event.setEndDateTime(LocalDateTime.now().plusDays(1));
        events.add(event);

        when(authenticationService.getIdFromCookie(any(HttpServletRequest.class))).thenReturn(user.getId().toString());
        when(userService.getUserById(String.valueOf(user.getId()))).thenReturn(user);
        when(eventRepository.findByOwner(user)).thenReturn(events);

        List<EventResponse> eventResponses = eventService.getEventsByOwner(String.valueOf(user.getId()));

        assertEquals(1, eventResponses.size());
        assertEquals("Test Event", eventResponses.get(0).getName());
    }

    @Test
    void testGetAllEvents() {
        List<Event> events = new ArrayList<>();
        Event event = new Event();
        event.setName("Test Event");
        event.setEndDateTime(LocalDateTime.now().plusDays(1));
        events.add(event);

        when(eventRepository.findAll()).thenReturn(events);

        List<EventResponse> eventResponses = eventService.getAllEvents();

        assertEquals(1, eventResponses.size());
        assertEquals("Test Event", eventResponses.get(0).getName());
    }

    @Test
    void testGetAllEvents_whenEndDateTimeIsAfterThanNow() {
        List<Event> events = new ArrayList<>();
        Event event = new Event();
        event.setName("Test Event");
        event.setEndDateTime(LocalDateTime.now().minusDays(1));
        events.add(event);

        when(eventRepository.findAll()).thenReturn(events);

        List<EventResponse> eventResponses = eventService.getAllEvents();

        assertEquals(0, eventResponses.size());
    }

    @Test
    void testConvertToGeoJson() {
        List<Event> events = new ArrayList<>();
        Event event = new Event();
        event.setName("Test Event");
        event.setLatitude(34.0522);
        event.setLongitude(-118.2437);
        event.setTag(EventTag.PARTY);
        events.add(event);

        GeoJsonWrapper geoJsonWrapper = eventService.convertToGeoJson(events);

        assertEquals("FeatureCollection", geoJsonWrapper.getType());
        assertEquals(1, geoJsonWrapper.getFeatures().size());
        assertEquals("Test Event", geoJsonWrapper.getFeatures().get(0).getProperties().getName());
    }

    @Test
    void getImageData_eventExists_returnsImageData() {
        Event event = new Event();
        event.setEventImage(new byte[]{});
        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.of(event));

        byte[] imageData = eventService.getImageData(UUID.randomUUID());

        assertNotNull(imageData);
    }

    @Test
    void getAllGEOEvents_eventsExist_returnsGeoJsonWrapper() {
        when(eventRepository.findAll()).thenReturn(new ArrayList<>());

        GeoJsonWrapper geoJsonWrapper = eventService.getAllGEOEvents();

        assertNotNull(geoJsonWrapper);
    }

    @Test
    void getEventById_eventExists_returnsEvent() {
        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Event()));

        Event event = eventService.getEventById(UUID.randomUUID());

        assertNotNull(event);
    }

    @Test
    void getEventById_eventDoesNotExist_throwsResourceNotFoundException() {
        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(UUID.randomUUID()));
    }

    @Test
    public void deleteEvent_shouldCallEventRepositoryDeleteMethod() {
        // Given
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testUser");
        Event event = new Event();
        event.setName("Test Event");

        // When
        eventService.deleteEvent(event);

        // Then
        verify(eventRepository).delete(event);
    }

    @Test
    void updateParticipants_validParameters_updatesParticipants() {
        Event event = new Event();
        User owner = new User();
        owner.setUsername("username");
        event.setOwner(owner);
        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.of(event));

        eventService.updateParticipants(UUID.randomUUID(), 10);

        verify(eventRepository, times(1)).updateParticipantsById(any(UUID.class), anyInt());
    }

    @Test
    void updateParticipants_invalidParameters_throwsInvalidParameterException() {
        Event event = new Event();
        User owner = new User();
        owner.setUsername("username");
        event.setOwner(owner);
        when(eventRepository.findById(any(UUID.class))).thenReturn(Optional.of(event));

        assertThrows(java.security.InvalidParameterException.class, () -> eventService.updateParticipants(UUID.randomUUID(), -1));
    }
}