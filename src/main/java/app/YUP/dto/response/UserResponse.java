package app.YUP.dto.response;

import app.YUP.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represents the data transfer object (DTO) for a user response.
 * It is used to transfer data between processes or systems and can carry information for the user details.
 * The class is annotated with Lombok annotations to automatically generate boilerplate code like getters, setters, constructors, and builders.
 *
 * @version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    /**
     * The first name of the user.
     */
    private String firstname;

    /**
     * The last name of the user.
     */
    private String lastname;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * The email of the user.
     */
    private String email;

    /**
     * The role of the user.
     * This is an enum that represents the role of the user.
     */
    private Role role;

    /**
     * The internal status of the user.
     * This is a boolean that represents whether the user is registered internally or not.
     */
    private boolean internal;

}