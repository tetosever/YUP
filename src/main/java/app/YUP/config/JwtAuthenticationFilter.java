package app.YUP.config;

import app.YUP.exception.CookieNotFoundException;
import app.YUP.exception.ResourceNotFoundException;
import app.YUP.model.ExternalUser;
import app.YUP.model.InternalUser;
import app.YUP.service.ExternalUserService;
import app.YUP.service.InternalUserService;
import app.YUP.service.JwtService;
import app.YUP.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * A Spring Security filter that authenticates requests using JWT tokens.
 *
 * This filter extracts the JWT token from the request headers or cookies, and then uses it to authenticate the user. If the
 * token is valid, the user is authenticated using Spring Security's authentication manager.
 *
 * If the user is successfully authenticated, the filter adds the user details to the security context, and sets the
 * authentication object in the security context. This allows the user to be accessed by other Spring Security
 * components, such as authorization managers.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String TOKEN_COOKIE_NAME = "JWT";

    private final JwtService jwtService;
    private final UserService userService;
    private final InternalUserService internalUserService;
    private final ExternalUserService externalUserService;

    /**
     * This method is called by Spring when a request is received by the application. It checks if the request contains a
     * valid JWT token, and if so, authenticates the user using the token.
     *
     * If the user is successfully authenticated, the filter adds the user details to the security context, and sets the
     * authentication object in the security context. This allows the user to be accessed by other Spring Security
     * components, such as authorization managers.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractTokenFromCookie(request);

        try {
            if (token != null) {
                String idFromToken = jwtService.getIdFromToken(token);

                if (idFromToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtService.isTokenValid(token, userService.getUserById(idFromToken))) {
                        AbstractAuthenticationToken authToken;

                        if (internalUserService.existsById(idFromToken)) {
                            InternalUser internalUser = internalUserService.getUserById(idFromToken);

                            authToken = new UsernamePasswordAuthenticationToken(
                                    internalUser, null, internalUser.getAuthorities());
                        } else {
                            ExternalUser externalUser = externalUserService.getUserById(idFromToken);

                            authToken = new UserAuthenticationToken(externalUser, externalUser.getAuthorities());
                        }

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } else {
                    logger.info("Token is expired");
                    throw new ExpiredJwtException(null, null, "Token is expired");
                }
            }
        } catch (ExpiredJwtException | ResourceNotFoundException exception) {
            deleteCookie(request, response);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the request cookies.
     *
     * @param request the HTTP request
     * @return the JWT token, or null if the token could not be found
     */
    protected String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .map(value -> value.startsWith("Bearer=")? value.substring("Bearer=".length()) : value)
                .orElse(null);
    }

    /**
     * Deletes the JWT cookie from the response.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     */
    private void deleteCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            int i = 0;
            while (i < cookies.length && !cookies[i].getName().equals("JWT")) {
                i++;
            }

            if (i >= cookies.length) {
                throw new CookieNotFoundException("Cookie not found");
            } else {
                Cookie deleteCookie = new Cookie(cookies[i].getName(), null);
                deleteCookie.setPath("/");
                deleteCookie.setHttpOnly(true);
                deleteCookie.setMaxAge(0);
                response.addCookie(deleteCookie);
            }
        } else {
            throw new CookieNotFoundException("Cookie not found");
        }
    }
}