package app.YUP.service;

import app.YUP.Enum.EventTag;
import app.YUP.Enum.Role;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.model.Event;
import app.YUP.model.Reservation;
import app.YUP.model.User;
import app.YUP.repository.EventRepository;
import app.YUP.repository.ReservationRepository;
import app.YUP.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EventServiceIT {
    @Autowired
    private EventService eventService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    public void setup() {
        // Cleanup the database before each test
        userRepository.deleteAll();
        eventRepository.deleteAll();
        reservationRepository.deleteAll();
    }

    @Test
    public void testGetEventById_WithValidId_ReturnsEvent() {
        User user = User.builder()
                .firstname("John")
                .lastname("Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .role(Role.LOGGED_USER)
                .build();
        userRepository.save(user);

        Event event = Event.builder()
                .name("Test Event")
                .description("Test Description")
                .startDateTime(LocalDateTime.now().plusHours(1))
                .endDateTime(LocalDateTime.now().plusHours(2))
                .location("Test Location")
                .participantsMaxNumber(100)
                .tag(EventTag.PARTY)
                .owner(user)
                .build();
        eventRepository.save(event);

        Event foundEvent = eventService.getEventById(event.getId());

        // Assert
        assertThat(foundEvent).isNotNull();
        assertEquals(event.getName(), foundEvent.getName());
        assertEquals(event.getDescription(), foundEvent.getDescription());
    }

    @Test
    public void getEventById_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        UUID invalidId = UUID.randomUUID();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> eventService.getEventById(invalidId));
    }

    @Test
    @Transactional
    public void testDeleteEvent() {
        User user = User.builder()
                .firstname("John")
                .lastname("Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .role(Role.LOGGED_USER)
                .build();
        userRepository.save(user);

        Event event = Event.builder()
                .name("Test Event")
                .description("Test Description")
                .startDateTime(LocalDateTime.now().plusHours(1))
                .endDateTime(LocalDateTime.now().plusHours(2))
                .location("Test Location")
                .participantsMaxNumber(100)
                .tag(EventTag.PARTY)
                .owner(user)
                .build();
        eventRepository.save(event);

        Event savedEvent = eventRepository.findById(event.getId()).orElse(null);
        assertThat(savedEvent).isNotNull();

        eventService.deleteEvent(savedEvent);

        Event deletedEvent =eventRepository.findById(event.getId()).orElse(null);
        assertThat(deletedEvent).isNull();
    }
    @Test
    @Transactional
    public void testDeleteEventAndCorreletedEntities() throws InterruptedException  {
        User user = User.builder()
                .firstname("John")
                .lastname("Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .role(Role.LOGGED_USER)
                .build();
        userRepository.save(user);

        User user2 = User.builder()
                .firstname("John")
                .lastname("Doe")
                .username("johndoe2")
                .email("johndoe2@example.com")
                .role(Role.LOGGED_USER)
                .build();
        userRepository.save(user2);

        User user3 = User.builder()
                .firstname("John")
                .lastname("Doe")
                .username("johndoe3")
                .email("johndoe2@example.com")
                .role(Role.LOGGED_USER)
                .build();
        userRepository.save(user3);

        Event event = Event.builder()
                .name("Test Event")
                .description("Test Description")
                .startDateTime(LocalDateTime.now().plusHours(1))
                .endDateTime(LocalDateTime.now().plusHours(2))
                .location("Test Location")
                .participantsMaxNumber(100)
                .tag(EventTag.PARTY)
                .owner(user)
                .build();
        eventRepository.save(event);

        Event event2 = Event.builder()
                .name("Test Event2")
                .description("Test Description2")
                .startDateTime(LocalDateTime.now().plusHours(1))
                .endDateTime(LocalDateTime.now().plusHours(2))
                .location("Test Location2")
                .participantsMaxNumber(100)
                .tag(EventTag.PARTY)
                .owner(user2)
                .build();
        eventRepository.save(event2);

        Event event3 = Event.builder()
                .name("Test Event3")
                .description("Test Description3")
                .startDateTime(LocalDateTime.now().plusHours(1))
                .endDateTime(LocalDateTime.now().plusHours(2))
                .location("Test Location3")
                .participantsMaxNumber(100)
                .tag(EventTag.PARTY)
                .owner(user2)
                .build();
        eventRepository.save(event3);


        reservationRepository.save(Reservation.builder()
                .user(user2)
                .event(event)
                .build());
        Thread.sleep(1000);
        reservationRepository.save(Reservation.builder()
                .user(user3)
                .event(event)
                .build());
        Thread.sleep(1000);
        reservationRepository.save(Reservation.builder()
                .user(user)
                .event(event2)
                .build());
        Thread.sleep(1000);
        reservationRepository.save(Reservation.builder()
                .user(user3)
                .event(event2)
                .build());
        Thread.sleep(1000);
        reservationRepository.save(Reservation.builder()
                .user(user3)
                .event(event3)
                .build());

        Event savedEvent = eventRepository.findById(event.getId()).orElse(null);
        assertThat(savedEvent).isNotNull();
        assertEquals(3, userRepository.findAll().size());
        assertEquals(3, eventRepository.findAll().size());
        assertEquals(5, reservationRepository.findAll().size());

        eventService.deleteEvent(event);

        Event deletedEvent = eventRepository.findById(event.getId()).orElse(null);
        assertThat(deletedEvent).isNull();
        assertEquals(3, userRepository.findAll().size());
        assertEquals(2, eventRepository.findAll().size());
        assertEquals(3, reservationRepository.findAll().size());
    }

    @Test
    @Transactional
    public void testGetEventParticipantsEmail_ExistingEventWithParticipants() throws InterruptedException {
        User user1 = User.builder()
                .firstname("John")
                .lastname("Doe")
                .username("johndoe1")
                .email("johndoe@example.com2")
                .role(Role.LOGGED_USER)
                .build();
        userRepository.save(user1);

        Event event = Event.builder()
                .name("Test Event")
                .description("Test Description")
                .startDateTime(LocalDateTime.now().plusHours(1))
                .endDateTime(LocalDateTime.now().plusHours(2))
                .location("Test Location")
                .participantsMaxNumber(100)
                .tag(EventTag.PARTY)
                .owner(user1)
                .build();
        eventRepository.save(event);

        User user2 = User.builder()
                .firstname("John")
                .lastname("Doe")
                .username("johndoe2")
                .email("johndoe@example.com1")
                .role(Role.LOGGED_USER)
                .build();
        userRepository.save(user2);


        Thread.sleep(1000);
        reservationRepository.save(Reservation.builder()
                .user(user1)
                .event(event)
                .build());
        Thread.sleep(1000);
        reservationRepository.save(Reservation.builder()
                .user(user2)
                .event(event)
                .build());

        // Calling the method
        Set<User> participants = eventService.getEventParticipants(event.getId());

        // Assertions
        assertNotNull(participants);
        assertEquals(2, participants.size());
        assertTrue(participants.contains(user1));
        assertTrue(participants.contains(user2));

        // Call the method
        Set<String> emails = eventService.getEventParticipantsEmails(participants);

        for (String email : emails) {
            System.out.println(email);
        }

        // Assertions
        assertEquals(2, emails.size());
        assertTrue(emails.contains("johndoe@example.com1"));
        assertTrue(emails.contains("johndoe@example.com2"));

    }


}
