package app.YUP.repository;

import app.YUP.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for the User entity.
 * This interface extends JpaRepository and provides methods to perform CRUD operations on the User entity.
 * It is annotated with @Repository to indicate that it is a Spring Data repository.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their username.
     *
     * @param username the username of the user
     * @return an Optional containing the user if found, or an empty Optional if not found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their ID.
     *
     * @param id the ID of the user
     * @return an Optional containing the user if found, or an empty Optional if not found
     */
    Optional<User> findById(UUID id);

    /**
     * Checks if a user exists by their username.
     *
     * @param username the username of the user
     * @return true if a user with the given username exists, false otherwise
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user exists by their email.
     *
     * @param email the email of the user
     * @return true if a user with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
