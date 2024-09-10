package app.YUP.service;

import app.YUP.Enum.Role;
import app.YUP.dto.request.UserUpdateRequest;
import app.YUP.dto.response.UserResponse;
import app.YUP.exception.InvalidParameterException;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.model.User;
import app.YUP.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void getUserById_found_returnsUser() {
        String userId = UUID.randomUUID().toString();
        User expectedUser = new User();
        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUserById(userId);

        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void getUserById_notFound_throwsResourceNotFoundException() {
        String userId = UUID.randomUUID().toString();

        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    public void getUserByUsername_found_returnsUser() {
        String username = "user1";
        User expectedUser = new User();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUserByUsername(username);

        assertNotNull(actualUser);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void getUserByUsername_notFound_throwsResourceNotFoundException() {
        String username = "nonexistent";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername(username));
    }

    @Test
    public void existsByUsername_exists_returnsTrue() {
        String username = "existingUser";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        boolean exists = userService.existsByUsername(username);

        assertTrue(exists);
    }

    @Test
    public void existsByUsername_notExists_returnsFalse() {
        String username = "nonexistentUser";

        when(userRepository.existsByUsername(username)).thenReturn(false);

        boolean exists = userService.existsByUsername(username);

        assertFalse(exists);
    }

    @Test
    public void mapEntityToDTO_correctMapping_returnsDTO() {
        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setRole(Role.LOGGED_USER);

        UserResponse response = userService.mapEntityToDTO(user);

        assertNotNull(response);
        assertEquals("John", response.getFirstname());
        assertEquals("Doe", response.getLastname());
        assertEquals("johndoe", response.getUsername());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals(Role.LOGGED_USER, response.getRole());
    }

    @Test
    public void deleteUser_shouldCallUserRepositoryDeleteMethodWithCorrectUser() {
        // Given
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("testUser");

        // When
        userService.deleteUser(user);

        // Then
        verify(userRepository).delete(user);
    }

    @Test
    public void deleteUser_shouldCallUserRepositoryDeleteMethodWithNullUser() {
        // Given
        User user = null;

        // When
        userService.deleteUser(user);

        // Then
        verify(userRepository).delete(user);
    }

    @Test
    public void checkIfUserExistsByUsername_WhenUserExists_ThrowsException() {
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThrows(ResourceNotFoundException.class, () -> userService.checkIfUserExistsByUsername("existingUser"));
    }

    @Test
    public void checkIfUserExistsByUsername_WhenUserDoesNotExist_DoesNotThrowException() {
        when(userRepository.existsByUsername("nonExistingUser")).thenReturn(false);

        assertDoesNotThrow(() -> userService.checkIfUserExistsByUsername("nonExistingUser"));
    }

    @Test
    public void updateUser_WithNewUsername_UpdatesUsername() {
        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setRole(Role.LOGGED_USER);

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("newUser");

        userService.updateUser(user, request);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void updateUser_WithExistingUsername_ThrowsException() {
        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setRole(Role.LOGGED_USER);

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("existingUser");

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(user, request));
    }

    @Test
    public void updateUser_WithInvalidEmail_ThrowsException() {
        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setRole(Role.LOGGED_USER);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setInternal(true);
        request.setEmail("invalidEmail");

        assertThrows(InvalidParameterException.class, () -> userService.updateUser(user, request));
    }

    @Test
    public void updateUser_WithValidEmail_UpdatesEmail() {
        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setUsername("johndoe");
        user.setEmail("john.doe@example.com");
        user.setRole(Role.LOGGED_USER);

        when(userRepository.save(any(User.class))).thenReturn(user);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setInternal(true);
        request.setEmail("valid.email@example.com");

        userService.updateUser(user, request);

        verify(userRepository, times(1)).save(user);
    }
}
