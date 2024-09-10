package app.YUP.service;

import app.YUP.config.CaptchaSettings;
import app.YUP.dto.request.ChangePasswordRequest;
import app.YUP.dto.request.RegistrationRequest;
import app.YUP.dto.response.CaptchaResponse;
import app.YUP.exception.CaptchaNotValidException;
import app.YUP.exception.InvalidParameterException;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.model.InternalUser;
import app.YUP.repository.InternalUserRepository;
import app.YUP.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InternalUserServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private InternalUserRepository internalUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InternalUserService internalUserService;

    private InternalUser internalUser;

    @BeforeEach
    public void setup() {
        internalUser = new InternalUser();
        internalUser.setPassword("oldPassword01");
    }

    @Test
    void createInternalUser_UserDoesNotExist_CreatesUser() {
        RegistrationRequest request = new RegistrationRequest(
                "john",
                "John",
                "Doe",
                "john@example.com",
                "Password123",
                "Password123");

        when(passwordEncoder.encode("Password123")).thenReturn("EncodedPassword");

        internalUserService.createInternalUser(request);

        verify(internalUserRepository).save(any(InternalUser.class));
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        String userId = UUID.randomUUID().toString();
        InternalUser expectedUser = new InternalUser();

        when(internalUserRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(expectedUser));

        InternalUser actualUser = internalUserService.getUserById(userId);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsException() {
        String userId = UUID.randomUUID().toString();

        when(internalUserRepository.findById(UUID.fromString(userId))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> internalUserService.getUserById(userId));
    }

    @Test
    public void changePassword_WithCorrectOldPassword_ChangesPassword() {
        when(passwordEncoder.matches("oldPassword01", internalUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("newPassword02", internalUser.getPassword())).thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword01");
        request.setNewPassword("newPassword02");
        request.setConfirmNewPassword("newPassword02");

        internalUserService.changePassword(request, internalUser);

        verify(internalUserRepository, times(1)).save(internalUser);
    }

    @Test
    public void changePassword_WithIncorrectOldPassword_ThrowsException() {
        when(passwordEncoder.matches("incorrectOldPassword", internalUser.getPassword())).thenReturn(false);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("incorrectOldPassword");
        request.setNewPassword("newPassword");
        request.setConfirmNewPassword("newPassword");

        assertThrows(InvalidParameterException.class, () -> internalUserService.changePassword(request, internalUser));
    }

    @Test
    public void changePassword_WithSameOldAndNewPassword_ThrowsException() {
        when(passwordEncoder.matches("oldPassword", internalUser.getPassword())).thenReturn(true);
        when(passwordEncoder.matches("oldPassword", internalUser.getPassword())).thenReturn(true);

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setOldPassword("oldPassword");
        request.setNewPassword("oldPassword");
        request.setConfirmNewPassword("oldPassword");

        assertThrows(InvalidParameterException.class, () -> internalUserService.changePassword(request, internalUser));
    }
}
