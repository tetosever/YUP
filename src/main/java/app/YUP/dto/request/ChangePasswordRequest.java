package app.YUP.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {
    /**
     * The old password of the user.
     * This should be securely hashed and not stored in plain text for security reasons.
     */
    private String oldPassword;

    /**
     * The confirmation password of the user trying to register.
     * This should match the password field to ensure the user has entered their password correctly.
     */
    private String newPassword;

    /**
     * The confirmation of new password of the user.
     * This should match the password field to ensure the user has entered their password correctly.
     */
    private String confirmNewPassword;
}
