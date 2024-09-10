package app.YUP.service;

import app.YUP.dto.request.UserUpdateRequest;
import app.YUP.dto.response.UserResponse;
import app.YUP.exception.InvalidParameterException;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.model.InternalUser;
import app.YUP.model.User;
import app.YUP.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * This class represents the service layer for the User entity.
 * It contains methods for creating, retrieving, and validating users.
 * The class is annotated with Spring's @Service annotation to indicate that it's a service component.
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class UserService {

    /**
     * The UserRepository instance used to interact with the database.
     */
    private final UserRepository userRepository;

    /**
     * Deletes a user from the database.
     *
     * @param user the user to delete. If the user is null, a ResourceNotFoundException is thrown.
     * @throws ResourceNotFoundException if the user to delete is null.
     */
    @Transactional
    public void deleteUser(User user) {
        userRepository.delete(user);
    }

    /**
     * Updates a user's information in the database.
     *
     * @param user the user to update. If the user is null, a ResourceNotFoundException is thrown.
     * @param userUpdateRequest the updated information for the user. If the username or email is null or empty,
     *                          an InvalidParameterException is thrown.
     *
     * @throws ResourceNotFoundException if the user to update is null.
     * @throws InvalidParameterException if the username or email is null or empty.
     */
    public void updateUser(User user, UserUpdateRequest userUpdateRequest) {
        if(!user.getUsername().equals(userUpdateRequest.getUsername())) {
            checkIfUserExistsByUsername(userUpdateRequest.getUsername());
            user.setUsername(userUpdateRequest.getUsername());
        }

        if(userUpdateRequest.isInternal()) {
            checkEmail(userUpdateRequest.getEmail());
            user.setEmail(userUpdateRequest.getEmail());
        }

        user.setFirstname(userUpdateRequest.getFirstname());
        user.setLastname(userUpdateRequest.getLastname());

        userRepository.save(user);
    }

    /**
     * Retrieves a user by their unique identifier (UUID).
     *
     * @param userId the unique identifier of the user to retrieve
     * @return the user with the given unique identifier
     * @throws ResourceNotFoundException if the user with the given unique identifier is not found
     */
    public User getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return the user with the given username
     * @throws ResourceNotFoundException if the user with the given username is not found
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Checks if a user with the given username exists in the database.
     *
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Checks if a user with the given username already exists in the system.
     *
     * @param username the username of the user to check
     * @throws ResourceNotFoundException if the username already exists
     */
    protected void checkIfUserExistsByUsername(String username) {
        if (existsByUsername(username)) {
            throw new ResourceNotFoundException("Username already exists");
        }
    }

    /**
     * Maps a User entity to a UserResponse DTO.
     *
     * @param user the User entity to map
     * @return the UserResponse DTO
     */
    public UserResponse mapEntityToDTO(User user) {
        return UserResponse.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    /**
     * Checks if a user with the given unique identifier (UUID) exists in the database.
     *
     * @param userId the unique identifier of the user to check
     * @return true if a user with the given unique identifier exists, false otherwise
     */
    public boolean existsById(UUID userId) {
        return userRepository.existsById(userId);
    }

    /**
     * Checks if the email is valid and not already in use.
     *
     * @param email the email to check
     * @throws InvalidParameterException if the email is invalid or already in use
     */
    protected void checkEmail(String email) {
        String regex = "^(.+)@(\\S+)$";
        if (!email.matches(regex)) {
            throw new InvalidParameterException("Invalid email format");
        }
    }
}
