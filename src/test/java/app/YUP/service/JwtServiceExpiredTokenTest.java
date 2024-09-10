package app.YUP.service;

import app.YUP.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("expired-token-test")
class JwtServiceExpiredTokenTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void isTokenValid_WithExpiredToken_ShouldReturnFalse() {
        String username = "test-username";
        User userDetails = User.builder()
                .username("test-username")
                .id(UUID.randomUUID())
                .build();
        String token = createToken(userDetails);

        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(token, userDetails);
        }, "Expected isTokenValid() to throw, but it didn't");

    }

    private String createToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());

        return jwtService.generateToken(claims, userDetails);
    }

}