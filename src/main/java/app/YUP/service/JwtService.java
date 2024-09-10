package app.YUP.service;

import app.YUP.exception.CookieNotFoundException;
import app.YUP.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class provides JWT (JSON Web Token) related functionalities.
 * It is responsible for generating, validating, and extracting information from JWT tokens.
 * It uses the io.jsonwebtoken library for handling JWTs.
 */
@Service
public class JwtService {

    /**
     * The secret key used for JWT encryption.
     * This key should be kept secure and not exposed in the codebase.
     */
    private static final String SECRET_KEY = "5310B7011DE4F67B717AA83DE873C4308B71F5ECC4C7A1CEAFFB5C6D1359DA29";

    /**
     * The expiration time (in milliseconds) of the JWT token.
     * This value is injected from the application properties.
     */
    @Value(value = "${jwt.token.expiration.time}")
    private long expirationTime;

    /**
     * Extracts the username from the JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String getIdFromToken(String token) {
        return extractClaimFromToken(token, Claims::getSubject);
    }

    /**
     * Extracts a claim from the JWT token.
     *
     * @param token the JWT token
     * @param claimsResolver the function that extracts the claim
     * @return the claim value extracted from the token
     */
    public <T> T extractClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaimsFrom(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Generates a JWT token for the given user details.
     *
     * @param user the user details
     * @return the generated JWT token
     */
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    /**
     * Generates a JWT token for the given user details and additional claims.
     *
     * @param extraClaims the additional claims to be included in the token
     * @param user the user details
     * @return the generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, User user) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getId().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Checks if the JWT token is valid for the given user details.
     *
     * @param token the JWT token
     * @param user the user details
     * @return true if the token is valid; false otherwise
     */
    public boolean isTokenValid(String token, User user) {
        final String userId = getIdFromToken(token);
        return userId.equals(user.getId().toString()) &&!isTokenExpired(token);
    }

    /**
     * Generates a cookie containing the JWT token.
     *
     * @param response the HTTP response
     * @param token the JWT token
     */
    protected void generateCookie(HttpServletResponse response, String token) {
        ResponseCookie jwtCookie = ResponseCookie.from("JWT", token)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
    }

    /**
     * Deletes the JWT cookie.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     */
    protected void deleteCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        Cookie jwtCookie = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("JWT")).findFirst()
                .orElseThrow(() -> new CookieNotFoundException("Cookie not found"));

        Cookie deleteCookie = new Cookie(jwtCookie.getName(), null);
        deleteCookie.setPath("/");
        deleteCookie.setHttpOnly(true);
        deleteCookie.setMaxAge(0);
        response.addCookie(deleteCookie);
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token the JWT token
     * @return true if the token has expired; false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpirationFromToken(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    private Date extractExpirationFromToken(String token) {
        return extractClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Extracts the claims from the JWT token.
     *
     * @param token the JWT token
     * @return the claims
     */
    protected Claims extractClaimsFrom(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Returns the signing key for JWT.
     *
     * @return the signing key
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes); // algorithm crypt for jwt token
    }
}