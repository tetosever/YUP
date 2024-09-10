package app.YUP.service;

import app.YUP.Enum.Role;
import app.YUP.model.InternalUser;
import app.YUP.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@SpringBootTest
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("test-username")
                .id(UUID.randomUUID())
                .build();
    }

    @Test
    void getUsernameFromToken_WithValidToken_ShouldReturnId() {
        String token = createToken(user);

        String actualUsername = jwtService.getIdFromToken(token);

        Assertions.assertEquals(user.getId().toString(), actualUsername);
    }

    @Test
    void extractClaimFromToken_WithValidTokenAndClaimResolver_ShouldReturnClaimValue() {
        String token = createToken(user);
        Function<Claims, String> claimsResolver = Claims::getSubject;

        String actualUsername = jwtService.extractClaimFromToken(token, claimsResolver);

        Assertions.assertEquals(user.getId().toString(), actualUsername);
    }

    @Test
    void generateToken_WithUserDetails_ShouldReturnToken() {
        String token = jwtService.generateToken(user);

        Assertions.assertNotNull(token);
    }

    @Test
    void generateToken_WithExtraClaims_ShouldReturnTokenWithExtraClaims() {
        // given
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("test-claim-key", "test-claim-value");

        String token = jwtService.generateToken(extraClaims, user);
        Claims claims = jwtService.extractClaimsFrom(token);

        Assertions.assertEquals("test-claim-value", claims.get("test-claim-key"));
    }

    @Test
    void isTokenValid_WithValidTokenAndUserDetails_ShouldReturnTrue() {
        String token = createToken(user);

        boolean isValid = jwtService.isTokenValid(token, user);

        Assertions.assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithInvalidToken_ShouldReturnFalse() {
        String token = createToken(user);
        user.setId(UUID.randomUUID());

        boolean isValid = jwtService.isTokenValid(token, user);

        Assertions.assertFalse(isValid);
    }

    private String createToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getId());

        return jwtService.generateToken(claims, userDetails);
    }

    @Test
    void generateToken_WithNullUserDetails_ShouldThrowException() {
        Assertions.assertThrows(NullPointerException.class, () -> jwtService.generateToken(null));
    }

    @Test
    void generateToken_WithExtraClaimsAndNullUserDetails_ShouldThrowException() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("test-claim-key", "test-claim-value");

        Assertions.assertThrows(NullPointerException.class, () -> jwtService.generateToken(extraClaims, null));
    }

    @Test
    void isTokenValid_WithNullToken_ShouldReturnFalse() {
        InternalUser internalUser =
                InternalUser.builder()
                        .username("username")
                        .password("password")
                        .role(Role.LOGGED_USER)
                        .build();

        Assertions.assertThrows(IllegalArgumentException.class, () ->
                jwtService.isTokenValid(null, internalUser));
    }

    @Test
    void isTokenValid_WithNullUserDetails_ShouldReturnFalse() {
        InternalUser internalUser =
                InternalUser.builder()
                        .id(UUID.randomUUID())
                        .username("username")
                        .password("password")
                        .role(Role.LOGGED_USER)
                        .build();

        String token = jwtService.generateToken(internalUser);

        Assertions.assertThrows(NullPointerException.class, () ->
                jwtService.isTokenValid(token, null));
    }
}