package app.YUP.model;

import app.YUP.Enum.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * This class represents a user in the application.
 * It is annotated with JPA annotations to map it to a database table.
 * It also implements the UserDetails interface from Spring Security to provide user authentication and authorization.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    /**
     * The unique identifier of the user.
     * It is generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * The username of the user.
     * It must be unique and cannot be null.
     */
    @Column(unique = true)
    @NotBlank(message = "Username is mandatory")
    private String username;

    /**
     * The firstname of the user.
     * It cannot be null.
     */
    @NotBlank(message = "Firstname is mandatory")
    private String firstname;

    /**
     * The lastname of the user.
     * It cannot be null.
     */
    @NotBlank(message = "Lastname is mandatory")
    private String lastname;

    /**
     * The email of the user.
     * It cannot be null.
     */
    @NotBlank(message = "Email is mandatory")
    private String email;

    /**
     * The role of the user.
     * It is stored as an enum and mapped to the database.
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * This field represents the reservations made by a user.
     * It is a Set of Reservation objects, meaning each user can make multiple reservations.
     * <p>
     * The @OneToMany annotation indicates that this is a one-to-many relationship,
     * i.e., one user can make multiple reservations.
     * <p>
     * The 'mappedBy = "user"' attribute indicates that the 'user' field in the Reservation entity
     * is the owner of the relationship (i.e., it contains the foreign key).
     * <p>
     * The 'cascade = CascadeType.REMOVE' attribute means that when a User entity is deleted,
     * all its associated Reservation entities will also be deleted from the database.
     * <p>
     * The 'fetch = FetchType.LAZY' attribute means that the reservations are loaded only when they are explicitly requested,
     * improving performance.
     * <p>
     * The 'orphanRemoval = true' attribute means that when a Reservation entity is removed from this set,
     * it will also be deleted from the database.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Reservation> reservation;


    /**
     * Represents a one-to-many relationship between a User entity and multiple Event entities.
     * <p>
     * This relationship indicates that a single user can own multiple events.
     * <p>
     * The 'mappedBy = "owner"' attribute indicates that the 'owner' field in the Event entity
     * is the owning side of the relationship. This means that the 'owner' field in the Event entity
     * contains the foreign key that references the primary key of the User entity.
     * <p>
     * The 'cascade = CascadeType.REMOVE' attribute specifies that when a User entity is removed,
     * all associated Event entities will also be removed from the database. This ensures that when
     * a user is deleted, all events owned by that user are also deleted, preventing orphaned records.
     * <p>
     * The 'fetch = FetchType.LAZY' attribute specifies that associated Event entities are loaded lazily
     * when accessed. This means that the events are not loaded immediately when the user entity is fetched
     * from the database, but are instead loaded on-demand when they are accessed for the first time.
     * Lazy loading can help improve performance by reducing the amount of data fetched from the database
     * when querying for user entities.
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private Set<Event> events;

    /**
     * Returns a collection of granted authorities based on the user's role.
     * This method is required by the UserDetails interface.
     *
     * @return a collection of granted authorities
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }


}
