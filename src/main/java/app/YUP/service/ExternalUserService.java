package app.YUP.service;

import app.YUP.Enum.LoginProvider;
import app.YUP.Enum.Role;
import app.YUP.dto.request.UserUpdateRequest;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.externalModel.EmailDetails;
import app.YUP.externalService.EmailService;
import app.YUP.model.ExternalUser;
import app.YUP.model.InternalUser;
import app.YUP.repository.ExternalUserRepository;
import app.services.model.ExternalProviderUser;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

/**
 * Service class for managing external users.
 */
@Service
@RequiredArgsConstructor
public class ExternalUserService {

    /**
     * Service for managing users.
     */
    private final UserService userService;

    /**
     * Repository for external users.
     */
    private final ExternalUserRepository externalUserRepository;

    /**
     * Service for managing JWT tokens.
     */
    private final JwtService jwtService;

    /**
     * Service for sending emails.
     */
    private final EmailService emailService;

    /**
     * Creates a new external user or updates an existing one.
     *
     * @param response The HTTP response to set the JWT cookie.
     * @param externalProviderUser The external user details from the provider.
     */
    @Transactional
    public void createExternalUser(HttpServletResponse response, ExternalProviderUser externalProviderUser) {
        ExternalUser externalUser;
        Optional<ExternalUser> optionalExternalUser =
                externalUserRepository.findByExternalId(externalProviderUser.getSub());

        if (optionalExternalUser.isEmpty()) {
            externalUser = ExternalUser.builder()
                    .provider(setLoginProvider(externalProviderUser.getRegistrationId()))
                    .externalId(externalProviderUser.getSub())
                    .email(externalProviderUser.getEmail())
                    .username(createUsername(externalProviderUser))
                    .firstname(externalProviderUser.getGivenName())
                    .lastname(externalProviderUser.getFamilyName())
                    .role(Role.LOGGED_USER)
                    .build();

            externalUser = externalUserRepository.save(externalUser);
        } else {
            externalUser = optionalExternalUser.get();
        }

        var jwtToken = jwtService.generateToken(externalUser);

        jwtService.generateCookie(response, jwtToken);

        EmailDetails details = new EmailDetails(externalUser.getEmail(), "<html>...</html>", "Welcome YUPper!", "");
        emailService.sendMailWithoutAttachment(details);
    }

    public void updateUser(ExternalUser externalUser, UserUpdateRequest userUpdateRequest) {
        userService.checkIfUserExistsByUsername(userUpdateRequest.getUsername());

        externalUser.setFirstname(userUpdateRequest.getFirstname());
        externalUser.setLastname(userUpdateRequest.getLastname());
        externalUser.setUsername(userUpdateRequest.getUsername());

        externalUserRepository.save(externalUser);
    }

    /**
     * Retrieves a user by their userId.
     *
     * @param userId The userId of the user to retrieve.
     * @return The user with the given userId.
     * @throws ResourceNotFoundException If the user is not found.
     */
    public ExternalUser getUserById(String userId) {
        return externalUserRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Checks if a user exists by their userId.
     *
     * @param userId The userId of the user to check.
     * @return True if the user exists, false otherwise.
     */
    public boolean existsById(String userId) {
        return externalUserRepository.existsById(UUID.fromString(userId));
    }

    /**
     * Sets the login provider for the external user.
     *
     * @param registrationId The registrationId of the external user.
     * @return The corresponding login provider.
     * @throws IllegalArgumentException If the registrationId is invalid.
     */
    private LoginProvider setLoginProvider(String registrationId) {
        return switch (registrationId) {
            case "google" -> LoginProvider.GOOGLE;
            default -> throw new IllegalArgumentException("Invalid registrationId");
        };
    }

    /**
     * Creates a unique username for the external user.
     *
     * @param externalProviderUser The external user details.
     * @return The unique username.
     */
    private String createUsername(ExternalProviderUser externalProviderUser) {
        Random random = new Random();
        String username = externalProviderUser.getEmail().split("@")[0];

        while (userService.existsByUsername(username)) {
            if (!username.contains("@")) {
                username = username.concat("@");
            }
            username = username.concat(random.nextInt(8) + 1 + "");
        }

        return username;
    }
}