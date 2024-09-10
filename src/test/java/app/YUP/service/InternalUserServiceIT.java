package app.YUP.service;

import app.YUP.Enum.Role;
import app.YUP.dto.request.ChangePasswordRequest;
import app.YUP.dto.request.RegistrationRequest;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.model.InternalUser;
import app.YUP.model.Reservation;
import app.YUP.repository.InternalUserRepository;
import app.YUP.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class InternalUserServiceIT {

    @Autowired
    private InternalUserService internalUserService;

    @Autowired
    private InternalUserRepository internalUserRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        internalUserRepository.deleteAll();
    }

    @Test
    void testCreateInternalUser() {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "newuser", "First", "Last", "newuser@example.com", "password123", "password123"
        );

        internalUserService.createInternalUser(registrationRequest);

        InternalUser result = internalUserRepository.findByUsername("newuser").orElse(null);
        assertNotNull(result);
        assertTrue(passwordEncoder.matches("password123", result.getPassword()));
    }

    @Test
    void testCreateInternalUser_whenUsernameAlreadyExists() {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "newuser", "First", "Last", "newuser@example.com", "password123", "password123"
        );

        internalUserService.createInternalUser(registrationRequest);

        InternalUser result = internalUserRepository.findByUsername("newuser").orElse(null);
        assertNotNull(result);
        assertTrue(passwordEncoder.matches("password123", result.getPassword()));

        assertThrows(ResourceNotFoundException.class, () ->
                internalUserService.createInternalUser(registrationRequest));
    }

    @Test
    void testChangePassword() {
        InternalUser existingUser = InternalUser.builder()
                .username("existingUser")
                .firstname("First")
                .lastname("Last")
                .email("existingUser@example.com")
                .role(Role.LOGGED_USER)
                .password(passwordEncoder.encode("oldPassword01"))
                .reservation(new HashSet<>())
                .build();

        existingUser = internalUserRepository.save(existingUser);

        ChangePasswordRequest request = new ChangePasswordRequest(
                "oldPassword01", "newPassword123", "newPassword123"
        );

        internalUserService.changePassword(request, existingUser);

        InternalUser updatedUser = internalUserRepository.findById(existingUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("newPassword123", updatedUser.getPassword()));
    }

    @Test
    void testGetUserById() {
        InternalUser existingUser = InternalUser.builder()
                .username("existingUser")
                .firstname("First")
                .lastname("Last")
                .email("existingUser@example.com")
                .role(Role.LOGGED_USER)
                .password(passwordEncoder.encode("oldPassword01"))
                .reservation(new HashSet<>())
                .build();
        existingUser = internalUserRepository.save(existingUser);

        InternalUser foundUser = internalUserService.getUserById(existingUser.getId().toString());
        assertEquals("existingUser", foundUser.getUsername());
    }


}
