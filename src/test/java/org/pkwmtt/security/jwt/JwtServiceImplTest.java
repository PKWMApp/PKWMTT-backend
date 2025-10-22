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
import org.pkwmtt.examCalendar.entity.SuperiorGroup;
import org.pkwmtt.security.jwt.utils.JwtUtils;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;
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
    void generateAccessToken_shouldCreateNonEmptyModeratorAccessToken() {
        Representative user = getExampleRepresentative();

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void getUserIdFromToken_shouldReturnCorrectId() {
        Representative user = getExampleRepresentative();

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(user);
        String id = jwtService.getSubject(token);
        assertEquals("11111111-2222-3333-4444-555555555555", id);
    }

    @Test
    void extractRoleFromToken_shouldReturnCorrectRole() {
        Representative user = getExampleRepresentative();

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(user);
        String roleClaim = jwtService.extractClaim(token, claims -> claims.get("role", String.class));
        assertEquals("ROLE_REPRESENTATIVE", roleClaim);
    }

    @Test
    void extractGroupFromToken_shouldReturnCorrectGroup() {
        Representative user = getExampleRepresentative();

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(user);
        String groupClaim = jwtService.extractClaim(token, claims -> claims.get("group", String.class));
        assertEquals("GROUP1", groupClaim);
    }

    @Test
    void validateAccessToken_shouldReturnTrueForValidModeratorAccessToken() {
        Representative user = getExampleRepresentative();

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(user);
        Representative mockUser = mock(Representative.class);
        when(mockUser.getRepresentativeId()).thenReturn(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        assertTrue(jwtService.validateAccessToken(token, mockUser));
    }

    @Test
    void validateAccessToken_shouldReturnFalseForInvalidEmail() {
        Representative user = getExampleRepresentative();

        when(jwtUtils.getExpirationMs()).thenReturn(TimeUnit.MINUTES.toMillis(5));

        String token = jwtService.generateAccessToken(user);
        Representative mockUser = mock(Representative.class);
        when(mockUser.getRepresentativeId()).thenReturn(UUID.fromString("22222222-2222-2222-2222-555555555555"));
        assertFalse(jwtService.validateAccessToken(token, mockUser));
    }

    @Test
    void validateAccessToken_shouldReturnFalseForExpiredModeratorAccessToken() {
        Representative user = getExampleRepresentative();

        long pastExpiration = System.currentTimeMillis() - 1000;
        String expiredToken = Jwts.builder()
                .subject(user.getRepresentativeId().toString())
                .claim("group", user.getSuperiorGroup())
                .claim("role", "ROLE_REPRESENTATIVE")
                .issuedAt(new Date(System.currentTimeMillis() - 2000))
                .expiration(new Date(pastExpiration))
                .signWith(jwtService.decodeSecretKey())
                .compact();

        Representative mockUser = mock(Representative.class);

        assertFalse(jwtService.validateAccessToken(expiredToken, mockUser));
    }

    @Test
    void getUserEmailFromToken_shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.value";
        assertThrows(JwtException.class, () -> jwtService.getSubject(invalidToken));
    }

    private static Representative getExampleRepresentative() {
        return Representative.builder()
                .representativeId(UUID.fromString("11111111-2222-3333-4444-555555555555"))
                .email("user@example.com")
                .superiorGroup(
                        SuperiorGroup.builder().name("GROUP1").build()
                ).build();
    }
}
