package org.pkwmtt.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pkwmtt.examCalendar.entity.Representative;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.security.jwt.dto.RepresentativeDTO;
import org.pkwmtt.security.jwt.utils.JwtUtils;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() {

        byte[] keyBytes = new byte[32];
        for (int i = 0; i < 32; i++) keyBytes[i] = (byte) i;
        String secretBase64 = Base64.getEncoder().encodeToString(keyBytes);

        when(jwtUtils.getSecret()).thenReturn(secretBase64);
    }

    @Test
    void generateAccessToken_shouldCreateNonEmptyAccessToken() {
        RepresentativeDTO user = new RepresentativeDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUserEmailFromToken_shouldReturnCorrectEmail() {
        RepresentativeDTO user = new RepresentativeDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(user);
        String email = jwtService.getSubject(token);
        assertEquals("user@example.com", email);
    }

    @Test
    void extractRoleFromToken_shouldReturnCorrectRole() {
        RepresentativeDTO user = new RepresentativeDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(user);
        String roleClaim = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("ADMIN", roleClaim);
    }

    @Test
    void extractGroupFromToken_shouldReturnCorrectGroup() {
        RepresentativeDTO user = new RepresentativeDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(user);
        String groupClaim = jwtService.extractClaim(token, claims -> claims.get("group", String.class));
        assertEquals("GROUP1", groupClaim);
    }

    @Test
    void validateAccessToken_shouldReturnTrueForValidAccessToken() {
        RepresentativeDTO userDTO = new RepresentativeDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(userDTO);
        Representative mockUser = mock(Representative.class);
        when(mockUser.getEmail()).thenReturn("user@example.com");
        assertTrue(jwtService.validateAccessToken(token, mockUser));
    }

    @Test
    void validateAccessToken_shouldReturnFalseForInvalidEmail() {
        RepresentativeDTO userDTO = new RepresentativeDTO()
                .setEmail("user@example.com")
                .setGroup("GROUP1")
                .setRole(Role.ADMIN);

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(userDTO);
        Representative mockUser = mock(Representative.class);
        when(mockUser.getEmail()).thenReturn("other@example.com");
        assertFalse(jwtService.validateAccessToken(token, mockUser));
    }

    @Test
    void validateAccessToken_shouldReturnFalseForExpiredAccessToken() {
        RepresentativeDTO user = new RepresentativeDTO()
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

        Representative mockUser = mock(Representative.class);
//        when(mockUser.getEmail()).thenReturn("user@example.com");

        assertFalse(jwtService.validateAccessToken(expiredToken, mockUser));
    }

    @Test
    void getUserEmailFromToken_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertThrows(JwtException.class, () -> jwtService.getSubject(invalidToken));
    }
}
