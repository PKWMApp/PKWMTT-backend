package org.pkwmtt.security.token;

import io.jsonwebtoken.Claims;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.security.token.dto.UserDTO;
import org.pkwmtt.security.token.entity.RefreshToken;

import java.util.UUID;
import java.util.function.Function;

public interface JwtService {
    String generateAccessToken(UserDTO user);
    String generateAccessToken(UUID uuid);
    String getNewRefreshToken(User user);
    RefreshToken verifyAndUpdateRefreshToken(String token);
    Boolean deleteRefreshToken(String token);
    Boolean validateAccessToken(String token, User user);
    Boolean validateAccessToken(String token, String uuid);
    String getSubject(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimResolver);
}
