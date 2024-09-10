package app.services.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * Questa classe funge da mapping fra l'oggetto di ritorno dal provider e il mio oggetto nel database.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalProviderUser implements OAuth2User {
    private OAuth2User oauth2User;
    @Getter
    private String registrationId;

    /**
     * Returns the attributes of the user provided by the external provider.
     *
     * @return a Map containing the attributes of the user.
     *
     * @see OAuth2User#getAttributes()
     */
    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    /**
     * Returns the authorities granted to the user represented by this object.
     *
     * @return a collection of {@link GrantedAuthority}s that the user is granted.
     *
     * @see OAuth2User#getAuthorities()
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }
    /**
     * Returns the provider unique identifier of the user represented by this object.
     *
     * @return the unique identifier of the user.
     */
    public String getSub() {
        return oauth2User.getAttribute("sub");
    }

    /**
     * The user full name in external provider.
     */
    @Override
    public String getName() {
        return oauth2User.getAttribute("name");
    }

    /**
     * The user first name in external provider.
     */
    public String getGivenName() {
        return oauth2User.getAttribute("given_name");
    }

    /**
     * The user last name in external provider.
     */
    public String getFamilyName() {
        return oauth2User.getAttribute("family_name");
    }

    /**
     * The user email in external provider.
     */
    public String getEmail() {
        return oauth2User.<String>getAttribute("email");
    }

}
