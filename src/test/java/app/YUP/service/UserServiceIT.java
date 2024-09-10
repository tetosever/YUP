package app.YUP.service;

import app.YUP.Enum.EventTag;
import app.YUP.Enum.Role;
import app.YUP.dto.response.UserResponse;
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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceIT {

    @Autowired
    private UserService userService;

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
    public void getUserById_WithValidId_ReturnsUser() {
        // Arrange
        User savedUser = new User();
        savedUser.setFirstname("Jane");
        savedUser.setLastname("Doe");
        savedUser.setUsername("janedoe");
        savedUser.setEmail("jane.doe@example.com");
        savedUser.setRole(Role.LOGGED_USER);
        savedUser = userRepository.save(savedUser);

        // Act
        User foundUser = userService.getUserById(savedUser.getId().toString());

        // Assert
        assertNotNull(foundUser);
        assertEquals("Jane", foundUser.getFirstname());
        assertEquals("Doe", foundUser.getLastname());
    }

    @Test
    public void getUserById_WithInvalidId_ThrowsResourceNotFoundException() {
        // Arrange
        String invalidId = UUID.randomUUID().toString();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(invalidId));
    }

    @Test
    public void createUser_AndRetrieve_ByUsername() {
        // Arrange
        User newUser = new User();
        newUser.setFirstname("John");
        newUser.setLastname("Doe");
        newUser.setUsername("johndoe");
        newUser.setEmail("john.doe@example.com");
        newUser.setRole(Role.LOGGED_USER);
        userRepository.save(newUser);

        // Act
        User retrievedUser = userService.getUserByUsername("johndoe");

        // Assert
        assertNotNull(retrievedUser);
        assertEquals("John", retrievedUser.getFirstname());
        assertEquals(Role.LOGGED_USER, retrievedUser.getRole());
    }

    @Test
    public void mapEntityToDTO_CorrectMapping_ReturnsDTO() {
        // Arrange
        User user = new User();
        user.setFirstname("Alice");
        user.setLastname("Smith");
        user.setUsername("alicesmith");
        user.setEmail("alice.smith@example.com");
        user.setRole(Role.LOGGED_USER);
        user = userRepository.save(user);

        // Act
        UserResponse response = userService.mapEntityToDTO(user);

        // Assert
        assertNotNull(response);
        assertEquals("Alice", response.getFirstname());
        assertEquals("Smith", response.getLastname());
        assertEquals("alicesmith", response.getUsername());
        assertEquals("alice.smith@example.com", response.getEmail());
        assertEquals(Role.LOGGED_USER, response.getRole());
    }

    @Test
    @Transactional
    public void testDeleteUser() {
        User user = User.builder()
                .firstname("John")
                .lastname("Doe")
                .username("johndoe")
                .email("johndoe@example.com")
                .role(Role.LOGGED_USER)
                .build();
        userRepository.save(user);

        User savedUser = userRepository.findByUsername("johndoe").orElse(null);
        assertThat(savedUser).isNotNull();

        userService.deleteUser(savedUser);

        User deletedUser = userRepository.findByUsername("testuser").orElse(null);
        assertThat(deletedUser).isNull();
    }

    @Test
    @Transactional
    public void testDeleteUserAndCorreletedEntities() {
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

        reservationRepository.save(Reservation.builder().user(user).event(event).build());
        reservationRepository.save(Reservation.builder().user(user2).event(event).build());


        User savedUser = userRepository.findByUsername("johndoe").orElse(null);
        assertThat(savedUser).isNotNull();
        assertEquals(2, userRepository.findAll().size());
        assertEquals(1, eventRepository.findAll().size());
        assertEquals(2, reservationRepository.findAll().size());

        userService.deleteUser(savedUser);

        User deletedUser = userRepository.findByUsername("johndoe").orElse(null);
        assertThat(deletedUser).isNull();

        assertEquals(1, userRepository.findAll().size());
        assertEquals(0, eventRepository.findAll().size());
        assertEquals(0, reservationRepository.findAll().size());
    }
}
