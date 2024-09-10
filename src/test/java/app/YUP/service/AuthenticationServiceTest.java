package app.YUP.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import app.YUP.Enum.Role;
import app.YUP.dto.request.AuthenticationRequest;
import app.YUP.dto.response.AuthenticationResponse;
import app.YUP.exception.CookieNotFoundException;
import app.YUP.exception.InvalidParameterException;
import app.YUP.model.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public class AuthenticationServiceTest {

    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "password";
    private static final String JWT_TOKEN = "jwtToken";

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_validCredentials_authenticationSuccess() throws Exception {
        // Arrange
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("user", "password");
        HttpServletResponse response = mock(HttpServletResponse.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken("user", "password");

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userService.getUserByUsername("user")).thenReturn(new User());
        when(jwtService.generateToken(any())).thenReturn("jwtToken");

        authenticationService.authenticate(authenticationRequest, response);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken(any(User.class));
        verify(jwtService).generateCookie(eq(response), eq("jwtToken"));
    }

    @Test
    void authenticate_invalidCredentials_throwsException() {
        // Arrange
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("user", "wrongpassword");
        HttpServletResponse response = mock(HttpServletResponse.class);

        when(authenticationManager.authenticate(any())).thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        Exception exception = assertThrows(InvalidParameterException.class, () -> {
            authenticationService.authenticate(authenticationRequest, response);
        });

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void logout_deletesCookie_successfully() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Act
        authenticationService.logout(request, response);

        // Assert
        verify(jwtService).deleteCookie(request, response);
    }

    @Test
    void getIdFromCookie_validCookie_returnsId() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        Cookie[] cookies = {new Cookie("JWT", "jwtToken")};

        when(request.getCookies()).thenReturn(cookies);
        when(jwtService.getIdFromToken("jwtToken")).thenReturn("123");

        String userId = authenticationService.getIdFromCookie(request);

        assertEquals("123", userId);
    }

    @Test
    void getIdFromCookie_noCookie_throwsException() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getCookies()).thenReturn(new Cookie[]{});

        assertThrows(CookieNotFoundException.class, () -> authenticationService.getIdFromCookie(request));
    }

    @Test
    void authenticate_invalidCredentials_throwInvalidParameterException() {
        var response = mock(HttpServletResponse.class);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("user", "password");

        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(InvalidParameterException.class, () -> authenticationService.authenticate(authenticationRequest, response));

        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void getUsernameFromCookie_cookieExists_returnUsername() {
        String userId = UUID.randomUUID().toString();
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JWT", "jwtToken")});
        when(jwtService.getIdFromToken(JWT_TOKEN)).thenReturn(userId);

        String userIdFromCookie = authenticationService.getIdFromCookie(request);

        assertEquals(userId, userIdFromCookie);
    }

    @Test
    void getUsernameFromCookie_cookieDoesNotExist_throwCookieNotFoundException() {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        assertThrows(CookieNotFoundException.class, () -> authenticationService.getIdFromCookie(request));
    }

    @Test
    void ifLoggedGetUsername_cookieExists_returnUsername() {
        User user = User.builder()
                .id(UUID.randomUUID())
                .firstname("test")
                .build();

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("JWT", JWT_TOKEN)});
        when(jwtService.getIdFromToken(JWT_TOKEN)).thenReturn(String.valueOf(user.getId()));
        when(userService.getUserById(String.valueOf(user.getId()))).thenReturn(user);

        String firstNameFromCookie = authenticationService.getFirstNameFromCookie(request);

        assertEquals(user.getFirstname(), firstNameFromCookie);
    }

    @Test
    void ifLoggedGetUsername_cookieDoesNotExist_returnNull() {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        String username = authenticationService.getFirstNameFromCookie(request);

        assertNull(username);
    }
}