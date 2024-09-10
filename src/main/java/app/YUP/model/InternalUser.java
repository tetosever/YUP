package app.YUP.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * This class represents an internal user in the system.
 * It extends the User class and implements the UserDetails interface from Spring Security.
 * This class contains additional information about the user, such as the password.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "int_users")
public class InternalUser extends User implements UserDetails {
    /**
     * The password of the user.
     * This field is annotated with @NotBlank to ensure that a password is mandatory.
     */
    @NotBlank(message = "Password is mandatory")
    private String password;

    /**
     * Returns true if the user's account is not expired.
     * This method is overridden from the UserDetails interface.
     *
     * @return true if the account is not expired, false otherwise
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Returns true if the user's account is not locked.
     * This method is overridden from the UserDetails interface.
     *
     * @return true if the account is not locked, false otherwise
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Returns true if the user's credentials are not expired.
     * This method is overridden from the UserDetails interface.
     *
     * @return true if the credentials are not expired, false otherwise
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Returns true if the user's account is enabled.
     * This method is overridden from the UserDetails interface.
     *
     * @return true if the account is enabled, false otherwise
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
