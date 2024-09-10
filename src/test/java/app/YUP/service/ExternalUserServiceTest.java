package app.YUP.service;

import app.YUP.Enum.LoginProvider;
import app.YUP.dto.response.UserResponse;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.externalModel.EmailDetails;
import app.YUP.externalService.EmailService;
import app.YUP.model.ExternalUser;
import app.YUP.repository.ExternalUserRepository;
import app.services.model.ExternalProviderUser;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ExternalUserServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private ExternalUserRepository externalUserRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private EmailService emailService;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private ExternalUserService externalUserService;

    @Test
    void createExternalUser_NewUser_SuccessfulCreation() {
        // Arrange
        OAuth2User oAuth2User = mock(OAuth2User.class);
        ExternalProviderUser providerUser = new ExternalProviderUser(oAuth2User, "google");

        ExternalUser expectedUser = new ExternalUser();
        expectedUser.setEmail("jane.doe@example.com");

        when(oAuth2User.getAttribute("sub")).thenReturn("sub123");
        when(oAuth2User.getAttribute("email")).thenReturn("jane.doe@example.com");
        when(oAuth2User.getAttribute("given_name")).thenReturn("Jane");
        when(oAuth2User.getAttribute("family_name")).thenReturn("Doe");
        when(externalUserRepository.findByExternalId(providerUser.getSub())).thenReturn(Optional.empty());
        when(externalUserRepository.save(any(ExternalUser.class))).thenReturn(expectedUser);
        when(userService.existsByUsername("jane.doe")).thenReturn(false);
        when(jwtService.generateToken(expectedUser)).thenReturn("jwtToken");

        // Act
        externalUserService.createExternalUser(response, providerUser);

        // Assert
        verify(externalUserRepository).save(any(ExternalUser.class));
        verify(emailService).sendMailWithoutAttachment(any(EmailDetails.class));
        verify(jwtService).generateCookie(response, "jwtToken");
    }

    @Test
    void createExternalUser_ExistingUser_DoesNotCreateNewUser() {
        OAuth2User oAuth2User = mock(OAuth2User.class);
        ExternalProviderUser providerUser = new ExternalProviderUser(oAuth2User, "google");

        when(oAuth2User.getAttribute("sub")).thenReturn("sub123");

        ExternalUser existingUser = new ExternalUser();
        existingUser.setEmail("jane.doe@example.com");

        when(externalUserRepository.findByExternalId(providerUser.getSub())).thenReturn(Optional.of(existingUser));
        when(jwtService.generateToken(any(ExternalUser.class))).thenReturn("jwtToken");

        // Act
        externalUserService.createExternalUser(response, providerUser);

        // Assert
        verify(externalUserRepository, never()).save(any(ExternalUser.class));
        verify(jwtService).generateCookie(response, "jwtToken");
    }

    @Test
    void getUserById_FoundUser_ReturnsUser() {
        String userId = UUID.randomUUID().toString();
        ExternalUser expectedUser = new ExternalUser();

        when(externalUserRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(expectedUser));

        ExternalUser actualUser = externalUserService.getUserById(userId);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserById_NotFound_ThrowsResourceNotFoundException() {
        String userId = UUID.randomUUID().toString();

        when(externalUserRepository.findById(UUID.fromString(userId))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> externalUserService.getUserById(userId));
    }
}
