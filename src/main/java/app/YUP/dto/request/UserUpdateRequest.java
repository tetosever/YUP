package app.YUP.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
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
     * The internal status of the user.
     * This is a boolean that represents whether the user is registered internally or not.
     */
    private boolean internal;
}
