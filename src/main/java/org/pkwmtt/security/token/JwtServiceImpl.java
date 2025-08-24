package org.pkwmtt.security.token;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.security.token.dto.UserDTO;
import org.pkwmtt.security.token.utils.JwtUtils;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

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
                .expiration((new Date(System.currentTimeMillis() + jwtUtils.getEXPIRATION_MS())))
                .signWith(decodeSecretKey())
                .compact();
    }


    /**
     * Decode a secret key for signing JWT.
     * The key is decoded from Base64 stored in JwtUtils configuration.
     *
     * @return secret key for JWT signing
     */
    private SecretKey decodeSecretKey(){
        byte[] decodedKey = Base64.getDecoder().decode(jwtUtils.getSECRET());
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
    public Boolean validateToken(String token) {
        try {
            // TODO: add logic to validate the token
            return true;
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
    public String getUserIdFromToken(String token) {
        // TODO: implement token parsing to extract subject/email
        return "";
    }
}
