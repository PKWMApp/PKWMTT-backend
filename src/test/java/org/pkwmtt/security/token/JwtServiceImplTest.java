package org.pkwmtt.security.token;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.security.token.dto.UserDTO;
import org.pkwmtt.security.token.utils.JwtUtils;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        JwtUtils jwtUtils = mock(JwtUtils.class);

        byte[] keyBytes = new byte[32];
        for (int i = 0; i < 32; i++) keyBytes[i] = (byte) i;
        String secretBase64 = Base64.getEncoder().encodeToString(keyBytes);

        when(jwtUtils.getSecret()).thenReturn(secretBase64);
        when(jwtUtils.getExpirationMs()).thenReturn(1000L * 60 * 60 * 24 * 30 * 6);

        jwtService = new JwtServiceImpl(jwtUtils);
    }

    @Test
    void generateToken_shouldCreateNonEmptyToken() {
        UserDTO user = new UserDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        String token = jwtService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUserEmailFromToken_shouldReturnCorrectEmail() {
        UserDTO user = new UserDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        String token = jwtService.generateToken(user);
        String email = jwtService.getUserEmailFromToken(token);
        assertEquals("user@example.com", email);
    }

    @Test
    void extractRoleFromToken_shouldReturnCorrectRole() {
        UserDTO user = new UserDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        String token = jwtService.generateToken(user);
        String roleClaim = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("ADMIN", roleClaim);
    }

    @Test
    void extractGroupFromToken_shouldReturnCorrectGroup() {
        UserDTO user = new UserDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        String token = jwtService.generateToken(user);
        String groupClaim = jwtService.extractClaim(token, claims -> claims.get("group", String.class));
        assertEquals("GROUP1", groupClaim);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        UserDTO userDTO = new UserDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        String token = jwtService.generateToken(userDTO);
        User mockUser = mock(User.class);
        when(mockUser.getEmail()).thenReturn("user@example.com");
        assertTrue(jwtService.validateToken(token, mockUser));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidEmail() {
        UserDTO userDTO = new UserDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        String token = jwtService.generateToken(userDTO);
        User mockUser = mock(User.class);
        when(mockUser.getEmail()).thenReturn("other@example.com");
        assertFalse(jwtService.validateToken(token, mockUser));
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        UserDTO user = new UserDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        long pastExpiration = System.currentTimeMillis() - 1000;
        String expiredToken = Jwts.builder()
                .subject(user.getEmail())
                .claim("group", user.getGroup())
                .claim("role", user.getRole())
                .issuedAt(new Date(System.currentTimeMillis() - 2000))
                .expiration(new Date(pastExpiration))
                .signWith(jwtService.decodeSecretKey())
                .compact();

        User mockUser = mock(User.class);
        when(mockUser.getEmail()).thenReturn("user@example.com");

        assertFalse(jwtService.validateToken(expiredToken, mockUser));
    }

    @Test
    void getUserEmailFromToken_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertThrows(JwtException.class, () -> jwtService.getUserEmailFromToken(invalidToken));
    }
}
