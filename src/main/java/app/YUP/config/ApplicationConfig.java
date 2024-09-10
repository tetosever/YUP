package app.YUP.config;

import app.YUP.repository.InternalUserRepository;
import app.YUP.service.ExternalUserService;
import app.services.config.ExternalProviderAuthenticationSuccessHandler;
import app.services.service.ExternalProviderUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for the application.
 * This class is responsible for setting up the security configuration of the application.
 * It defines beans for UserDetailsService, AuthenticationProvider, AuthenticationManager, and PasswordEncoder.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    /**
     * InternalUserRepository instance for accessing internal user data from the database.
     */
    private final InternalUserRepository internalUsersRepository;

    /**
     * Bean definition for UserDetailsService.
     * This service is used for loading user-specific data.
     * It throws UsernameNotFoundException if the user is not found in the database.
     *
     * @return the UserDetailsService instance
     */
    @Bean
    public UserDetailsService userDetailsService(InternalUserRepository internalUserRepository) {
        return username -> internalUserRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.isEnabled(),
                        user.isAccountNonExpired(),
                        user.isCredentialsNonExpired(),
                        user.isAccountNonLocked(),
                        AuthorityUtils.createAuthorityList("ROLE_USER"))) // Adatta gli authority secondo il tuo caso d'uso
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Bean definition for AuthenticationProvider.
     * This provider is used for authenticating a UserDetails.
     * It uses DaoAuthenticationProvider which retrieves user details from a UserDetailsService.
     *
     * @return the AuthenticationProvider instance
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService(internalUsersRepository));
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    /**
     * Bean definition for AuthenticationManager.
     * This manager is used for authenticating a user in the application.
     *
     * @param authenticationConfiguration the AuthenticationConfiguration instance
     * @return the AuthenticationManager instance
     * @throws Exception if an error occurs while getting the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Bean definition for PasswordEncoder.
     * This encoder (BCryptPasswordEncoder) is used for hashing passwords.
     *
     * @return the PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates an instance of the ExternalProviderUserService class.
     * This class is responsible for handling user operations related to external providers.
     *
     * @return an instance of ExternalProviderUserService
     */
    @Bean
    public ExternalProviderUserService externalProviderUserService() {
        return new ExternalProviderUserService();
    }

    /**
     * Creates an instance of the ExternalProviderAuthenticationSuccessHandler class.
     * This class is responsible for handling the success scenario of authentication with external providers.
     *
     * @param externalUserService an instance of ExternalUserService, used to perform operations related to external users
     * @return an instance of ExternalProviderAuthenticationSuccessHandler
     */
    @Bean
    public ExternalProviderAuthenticationSuccessHandler externalProviderAuthenticationSuccessHandler(
            ExternalUserService externalUserService) {
        return new ExternalProviderAuthenticationSuccessHandler(externalUserService);
    }

}