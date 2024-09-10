package app.YUP.config;

import app.YUP.model.User;
import app.YUP.service.InternalUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;


/**
 * Custom authentication token for the application. This class extends Spring's AbstractAuthenticationToken
 * and is used to represent the authenticated user during the security context.
 */
public class UserAuthenticationToken extends AbstractAuthenticationToken {

    /**
     * The authenticated user.
     */
    private final User user;

    /**
     * Constructor for UserAuthenticationToken.
     *
     * @param user The authenticated user.
     * @param authorities The authorities (roles) of the user.
     */
    public UserAuthenticationToken(User user, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.user = user;
        setAuthenticated(true); // Marks the token as authenticated
    }

    /**
     * Returns the credentials (usually a password) associated with this token.
     * In this case, it returns null since we don't have a password in our authentication process.
     *
     * @return null
     */
    @Override
    public Object getCredentials() {
        return null;
    }

    /**
     * Returns the principal (usually a User object) associated with this token.
     *
     * @return The authenticated user.
     */
    @Override
    public Object getPrincipal() {
        return user;
    }
}
