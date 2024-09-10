package app.YUP.service;

import app.YUP.dto.request.AuthenticationRequest;
import app.YUP.exception.CookieNotFoundException;
import app.YUP.exception.InvalidParameterException;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * This class represents the service layer for the Authentication entity.
 * It contains methods for authenticating users, logging out users, and managing JWT cookies.
 * The class is annotated with Spring's @Service annotation to indicate that it's a service component.
 *
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    /**
     * Authenticates a user using their username and password.
     * It generates a JWT token for the authenticated user and stores it in a cookie.
     *
     * @param authenticationRequest the authentication request containing the username and password
     * @param response              the HTTP response to set the JWT cookie
     * @throws InvalidParameterException if the username or password is invalid
     * @throws ResourceNotFoundException if the user is not found
     */
    public void authenticate(AuthenticationRequest authenticationRequest, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            ));
        } catch (Exception e) {
            throw new InvalidParameterException("Invalid username or password");
        }

        var user = userService.getUserByUsername(authenticationRequest.getUsername());

        var jwtToken = jwtService.generateToken(user);

        jwtService.generateCookie(response, jwtToken);
    }

    /**
     * Logs out a user by deleting the JWT cookie.
     *
     * @param request  the HTTP request object
     * @param response the HTTP response object
     * @throws CookieNotFoundException if the JWT cookie is not found
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        jwtService.deleteCookie(request, response);
    }
    /**
     * Retrieves the id from the JWT cookie.
     *
     * @param request the HTTP request to retrieve the cookies
     * @return the id from the JWT cookie
     * @throws CookieNotFoundException if the JWT cookie is not found
     */
    public String getIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        Cookie jwtCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("JWT")).findFirst()
                .orElseThrow(() -> new CookieNotFoundException("Cookie not found"));
        return jwtService.getIdFromToken(jwtCookie.getValue());

    }

    /**
     * This method retrieves the user ID from the cookie in the HttpServletRequest object.
     * It first attempts to extract the user ID from the cookie using the getIdFromCookie method.
     * If an exception occurs during the extraction process, it sets the user ID to null.
     *
     * @param request This is the HttpServletRequest object that contains the request details.
     * @return String This returns the user ID extracted from the cookie, or null if the extraction fails.
     */
    public String checkIdFromCookie(HttpServletRequest request) {
        String userId;

        try {
            userId = getIdFromCookie(request);
        } catch (Exception e) {
            userId = null;
        }

        return userId;
    }

    /**
     * Retrieves the first name if the user is logged in.
     *
     * @param request the HTTP request to retrieve the cookies
     * @return the first name of the user if logged in, otherwise null
     */
    public String getFirstNameFromCookie(HttpServletRequest request) {
        String userId;
        try {
            userId = userService.getUserById(getIdFromCookie(request)).getFirstname();
        } catch (CookieNotFoundException e) {
            userId = null;
        }

        return userId;
    }
}