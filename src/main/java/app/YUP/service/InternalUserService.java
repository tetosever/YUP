package app.YUP.service;

import app.YUP.Enum.Role;
import app.YUP.config.CaptchaSettings;
import app.YUP.dto.request.ChangePasswordRequest;
import app.YUP.dto.request.RegistrationRequest;
import app.YUP.dto.request.UserUpdateRequest;
import app.YUP.dto.response.CaptchaResponse;
import app.YUP.exception.CaptchaNotValidException;
import app.YUP.exception.InvalidParameterException;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.model.InternalUser;
import app.YUP.model.User;
import app.YUP.repository.InternalUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * Service class for managing internal users.
 */
@Service
@RequiredArgsConstructor
public class InternalUserService {

    private final UserService userService;
    private final InternalUserRepository internalUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final CaptchaSettings captchaSettings;

    /**
     * Creates a new internal user in the system.
     *
     * @param registrationRequest the user dto to create
     */
    public void createInternalUser(RegistrationRequest registrationRequest) {
        userService.checkIfUserExistsByUsername(registrationRequest.getUsername());
        userService.checkEmail(registrationRequest.getEmail());
        checkPassword(registrationRequest.getPassword(), registrationRequest.getConfirmPassword());

        InternalUser internalUser = InternalUser.builder()
                .username(registrationRequest.getUsername())
                .firstname(registrationRequest.getFirstname())
                .lastname(registrationRequest.getLastname())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .role(Role.LOGGED_USER)
                .build();

        internalUserRepository.save(internalUser);
    }

    /**
     * Changes the password of an internal user.
     *
     * @param changePasswordRequest the request containing the old and new passwords
     * @param internalUser the internal user whose password needs to be changed
     * @throws InvalidParameterException if the old password is incorrect or the new password does not meet the requirements
     */
    public void changePassword(ChangePasswordRequest changePasswordRequest, InternalUser internalUser) {
        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), internalUser.getPassword())) {
            throw new InvalidParameterException("Old password is incorrect");
        }
        if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), internalUser.getPassword())) {
            throw new InvalidParameterException("New password must be different from the old password");
        }
        if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmNewPassword())) {
            throw new InvalidParameterException("Confirm new password does not match the new password");
        }
        checkPassword(changePasswordRequest.getNewPassword(), changePasswordRequest.getConfirmNewPassword());
        internalUser.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        internalUserRepository.save(internalUser);
    }

    /**
     * Retrieves a user by their userId.
     *
     * @param userId the userId of the user to retrieve
     * @return the user with the given userId
     * @throws ResourceNotFoundException if the user is not found
     */
    public InternalUser getUserById(String userId) {
        return internalUserRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Checks if a user with the given userId exists in the system.
     *
     * @param userId the userId of the user to check
     * @return true if the user exists, false otherwise
     */
    public boolean existsById(String userId) {
        return internalUserRepository.existsById(UUID.fromString(userId));
    }

    /**
     * Check if captcha is valid.
     *
     * @param captcha the captcha response
     * @return true if the captcha is valid, false otherwise
     * @throws CaptchaNotValidException if the captcha validation fails
     */
    public boolean isValidCaptcha (String captcha) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://www.google.com/recaptcha/api/siteverify";
            String params = "?secret=" + captchaSettings.getSecret() + "&response=" + captcha;
            String completeUrl = url + params;
            CaptchaResponse resp = restTemplate.postForObject(completeUrl, null, CaptchaResponse.class);
            assert resp!= null;
            if (!resp.isSuccess()) {
                throw new CaptchaNotValidException("Captcha validation failed");
            }
            return true;
        } catch (Exception e) {
            throw new CaptchaNotValidException("Failed to verify captcha");
        }
    }

    /**
     * Checks if the password meets the requirements.
     *
     * @param password the password to check
     * @param confirmPassword the password confirmation
     * @throws InvalidParameterException if the password does not meet the requirements or does not match the confirmation
     */
    private void checkPassword(String password, String confirmPassword) {
        String regex = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$";
        if (!password.matches(regex)) {
            throw new InvalidParameterException(
                    "Password must contain at least 8 characters and at least one letter and one number");
        }
        if (!password.equals(confirmPassword)) {
            throw new InvalidParameterException(
                    "Passwords must match");
        }
    }
}

