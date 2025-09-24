package org.pkwmtt.security.token;

import io.jsonwebtoken.Claims;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.security.token.dto.UserDTO;

import java.util.UUID;
import java.util.function.Function;

public interface JwtService {
    String generateToken(UserDTO user);
    String generateToken(UUID uuid);
    Boolean validateToken(String token, User user);
    String getUserEmailFromToken(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimResolver);
}
