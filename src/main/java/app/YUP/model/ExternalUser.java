package app.YUP.model;

import app.YUP.Enum.LoginProvider;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * This class represents an external user in the system.
 * It extends the User class and contains additional information about the user, such as externalId and provider.
 *
 * @version 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ext_users")
public class ExternalUser extends User {

    /**
     * The unique identifier of the external user.
     * This field is marked as unique to ensure that each external user has a distinct identifier.
     * It is also marked as not nullable, meaning that it must have a value.
     *
     * @param externalId the unique identifier of the external user
     */
    @Column(unique = true)
    @NotBlank(message = "External id is mandatory")
    private String externalId;

    /**
     * The provider of the external user.
     * This field is an enumeration of type LoginProvider, which represents the source or service from which the user's information is obtained.
     *
     * @param provider the provider of the external user
     */
    @Enumerated(EnumType.STRING)
    private LoginProvider provider;
}
