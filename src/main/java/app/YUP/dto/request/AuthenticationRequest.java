package app.YUP.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the data transfer object (DTO) for an authentication request.
 * It is used to transfer data between processes or systems and can carry information for the username and password.
 * The class is annotated with Lombok annotations to automatically generate boilerplate code like getters, setters, constructors, and builders.
 *
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    /**
     * The username of the user trying to authenticate.
     * This is a unique identifier for each user.
     */
    private String username;

    /**
     * The password of the user trying to authenticate.
     * This should be securely hashed and not stored in plain text for security reasons.
     */
    private String password;

}