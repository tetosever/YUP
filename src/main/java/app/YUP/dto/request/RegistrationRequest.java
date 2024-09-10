package app.YUP.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the data transfer object (DTO) for a registration request.
 * It is used to transfer data between processes or systems and can carry information for the user registration details.
 * The class is annotated with Lombok annotations to automatically generate boilerplate code like getters, setters, constructors, and builders.
 *
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {

    /**
     * The username of the user trying to register.
     * This is a unique identifier for each user.
     */
    private String username;

    /**
     * The first name of the user trying to register.
     */
    private String firstname;

    /**
     * The last name of the user trying to register.
     */
    private String lastname;

    /**
     * The email of the user trying to register.
     * This should be a valid email address.
     */
    private String email;

    /**
     * The password of the user trying to register.
     * This should be securely hashed and not stored in plain text for security reasons.
     */
    private String password;

    /**
     * The confirmation password of the user trying to register.
     * This should match the password field to ensure the user has entered their password correctly.
     */
    private String confirmPassword;

}