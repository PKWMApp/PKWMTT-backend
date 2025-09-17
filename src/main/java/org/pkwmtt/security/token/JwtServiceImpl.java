package org.pkwmtt.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.security.token.dto.UserDTO;
import org.pkwmtt.security.token.utils.JwtUtils;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JwtUtils jwtUtils;

    /**
     * Generates a JWT token for a given user.
     * The token contains user's email, group, and role as claims,
     * and is signed with a secret key.
     *
     * @param user - required user data to include in token claims
     * @return signed JWT token as a String
     */
    @Override
    public String generateToken(UserDTO user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("group", user.getGroup())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration((new Date(System.currentTimeMillis() + jwtUtils.getExpirationMs())))
                .signWith(decodeSecretKey())
                .compact();
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
    @Override
    public Boolean validateToken(String token, User user) {
        try {
            final String userEmail = getUserEmailFromToken(token);
            return userEmail != null
                    && userEmail.equals(user.getEmail())
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
    @Override
    public String getUserEmailFromToken(String token) {
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
