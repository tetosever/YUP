package app.YUP.config;

import app.YUP.service.ExternalUserService;
import app.services.config.ExternalProviderAuthenticationSuccessHandler;
import app.services.service.ExternalProviderUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for Spring Security.
 * This class is responsible for setting up the security configuration of the application.
 * It defines the security filter chain and the URLs that are allowed to be accessed without authentication.
 * It is annotated with @Configuration to indicate that it is a Spring configuration class, @EnableWebSecurity to enable Spring Security's web security support, and @EnableMethodSecurity to enable method-level security.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Array of URLs that are allowed to be accessed without authentication.
     */
    private static final String[] WHITE_LIST_URL = {"/auth/api/login", "/auth/login", "/user/api/newUser", "/user/newUser", "/v3/api-docs/**", "/style/**", "/script/**", "/images/**", "/event/all", "/event/api/geo/all", "/error", "event/**", "/sendErrorEmail", "/home", "/user/checkIfUserExist"};

    /**
     * The JwtAuthenticationFilter instance for authenticating requests using JWT tokens.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * The AuthenticationProvider instance for authenticating a UserDetails.
     */
    private final AuthenticationProvider authenticationProvider;

    /**
     * The ExternalProviderUserService instance for authenticating a User by OAuth2.
     */
    private final ExternalProviderUserService externalProviderUserService;

    /**
     * The externalProviderAuthenticationSuccessHandler instance for manage response by external provider for
     * authentication.
     */
    private final ExternalProviderAuthenticationSuccessHandler externalProviderAuthenticationSuccessHandler;


    /**
     * Bean definition for SecurityFilterChain.
     * This chain is used for applying the security configurations to the incoming requests.
     * It disables CSRF, allows requests to the URLs in the white list, requires authentication for any other request, sets the session management policy to stateless, sets the authentication provider, and adds the JwtAuthenticationFilter before the UsernamePasswordAuthenticationFilter in the filter chain.
     *
     * @param http the HttpSecurity instance for building the security configuration
     * @return the SecurityFilterChain instance
     * @throws Exception if an error occurs while building the filter chain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeHttpRequests().requestMatchers(WHITE_LIST_URL).permitAll().anyRequest().authenticated().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authenticationProvider(authenticationProvider).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class).oauth2Login().loginPage("/auth/login").userInfoEndpoint().userService(externalProviderUserService).and().successHandler(externalProviderAuthenticationSuccessHandler);

        return http.build();
    }
}