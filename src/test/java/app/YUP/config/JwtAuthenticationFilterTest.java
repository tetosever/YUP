package app.YUP.config;

import app.YUP.Enum.Role;
import app.YUP.model.InternalUser;
import app.YUP.model.User;
import app.YUP.service.ExternalUserService;
import app.YUP.service.InternalUserService;
import app.YUP.service.JwtService;
import app.YUP.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private InternalUserService internalUserService;

    @Mock
    private ExternalUserService externalUserService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilterInternal_withValidToken_shouldAuthenticateUser() throws Exception {
        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("JWT", "Bearer=validToken")
        });

        InternalUser internalUser = InternalUser.builder()
                .id(UUID.randomUUID())
                .role(Role.LOGGED_USER)
                .build();

        when(jwtService.getIdFromToken("validToken")).thenReturn(String.valueOf(internalUser.getId()));
        when(userService.getUserById(String.valueOf(internalUser.getId()))).thenReturn(internalUser);
        when(jwtService.isTokenValid("validToken",
                userService.getUserById(String.valueOf(internalUser.getId())))).thenReturn(true);
        when(internalUserService.existsById(String.valueOf(internalUser.getId()))).thenReturn(true);
        when(internalUserService.getUserById(String.valueOf(internalUser.getId()))).thenReturn(internalUser);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilterInternal_withInvalidToken_shouldDeleteCookie() throws Exception {
        when(request.getCookies()).thenReturn(new Cookie[]{
                new Cookie("JWT", "Bearer=invalidToken")
        });

        InternalUser user = InternalUser.builder()
                .id(UUID.randomUUID())
                .build();

        when(jwtService.getIdFromToken("invalidToken")).thenReturn(String.valueOf(user.getId()));
        when(userService.getUserById("user")).thenReturn(user);
        when(jwtService.isTokenValid("invalidToken",
                userService.getUserById(String.valueOf(user.getId())))).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(0)).addCookie(any(Cookie.class));
    }

    @Test
    void doFilterInternal_noToken_shouldJustContinueChain() throws Exception {
        when(request.getCookies()).thenReturn(null); // No cookies

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}