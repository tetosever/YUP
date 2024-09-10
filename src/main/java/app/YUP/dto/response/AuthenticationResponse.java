package app.YUP.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the data transfer object (DTO) for an authentication response.
 * It is used to transfer data between processes or systems and can carry information for the authentication token.
 * The class is annotated with Lombok annotations to automatically generate boilerplate code like getters, setters, constructors, and builders.
 *
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    /**
     * The authentication token generated after successful authentication.
     * This token is used for subsequent requests to the server for authentication.
     */
    private String token;

}