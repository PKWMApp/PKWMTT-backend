package org.pkwmtt.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.Representative;
import org.pkwmtt.exceptions.InvalidRefreshTokenException;
import org.pkwmtt.security.jwt.refreshToken.entity.RefreshToken;
import org.pkwmtt.security.jwt.utils.JwtUtils;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtUtils jwtUtils;

    /**
     * Generates a JWT token for a given user.
     * The token contains user's email, group, and role as claims,
     * and is signed with a secret key.
     *
     * @param representative - required user data to include in token claims
     * @return signed JWT token as a String
     */
    public String generateAccessToken(Representative representative) {
        return Jwts.builder()
                .subject(representative.getRepresentativeId().toString())
                .claim("group", representative.getSuperiorGroup())
                .claim("role", "ROLE_REPRESENTATIVE")
                .issuedAt(new Date())
                .expiration((new Date(System.currentTimeMillis() + jwtUtils.getExpirationMs())))
                .signWith(decodeSecretKey())
                .compact();
    }

    public String generateModeratorAccessToken(UUID uuid) {
        return Jwts.builder()
                .subject(uuid.toString())
                .claim("role", "ROLE_MODERATOR")
                .issuedAt(new Date())
                .expiration((new Date(System.currentTimeMillis() + jwtUtils.getExpirationMs())))
                .signWith(decodeSecretKey())
                .compact();
    }

    public static String generateRefreshToken() {
        SecureRandom random = new SecureRandom();
        byte[] randomBytes = new byte[32];
        random.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }


    public static void validateRefreshToken(RefreshToken rt) throws  InvalidRefreshTokenException {
        if (rt.getExpires().isBefore(LocalDateTime.now()))
            throw new InvalidRefreshTokenException();
    }


    /**
     * Decode a secret key for signing JWT.
     * The key is decoded from Base64 stored in JwtUtils configuration.
     *
     * @return secret key for JWT signing
     */
    SecretKey decodeSecretKey(){
        byte[] decodedKey = Base64.getDecoder().decode(jwtUtils.getSecret());
        return Keys.hmacShaKeyFor(decodedKey);
    }

    /**
     * Validate a JWT token.
     * Attempts to parse the token; if parsing fails, the token is considered invalid.
     *
     * @param token JWT token string to validate
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateAccessToken(String token, Representative user) {
        try {
            final String userEmail = getSubject(token);
            return userEmail != null
                    && userEmail.equals(user.getEmail())
                    && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Validates an access token by checking if the token's subject matches the provided UUID
     * and if the token has not expired.
     *
     * @param token the JWT token string to validate
     * @param uuid the UUID to compare with the token's subject
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateModeratorAccessToken(String token, String uuid) {
        try {
            final String userid = getSubject(token);
            return userid != null
                    && userid.equals(uuid)
                    && !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    /**
     * Extracts the user identifier (email) from a JWT token.
     *
     * @param token JWT token to extract user from
     * @return user email from token
     */
    public String getSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token JWT token string
     * @return expiration date of the token
     */
    private Date getExpirationDateFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Checks whether a JWT token has expired.
     *
     * @param token JWT token string
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token){
        return getExpirationDateFromToken(token).before(new Date());
    }

    /**
     * Extracts a specific claim from a JWT token using a claim resolver function.
     *
     * @param <T> type of the claim
     * @param token JWT token string
     * @param claimResolver function to extract the desired claim from Claims
     * @return the extracted claim of type T
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * Parses the JWT token and returns all claims contained in its payload.
     * <p>
     * The method verifies the token signature using the secret key.
     *
     * @param token JWT token string
     * @return Claims object containing all claims from the token payload
     * @throws JwtException if the token is invalid or the signature does not match
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(decodeSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
