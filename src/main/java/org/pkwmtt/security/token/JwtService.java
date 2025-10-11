package org.pkwmtt.security.token;

import io.jsonwebtoken.Claims;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.security.moderator.Moderator;
import org.pkwmtt.security.token.dto.UserDTO;
import org.pkwmtt.security.token.entity.RefreshToken;
import org.pkwmtt.security.token.entity.UserRefreshToken;
import org.pkwmtt.security.token.repository.RefreshTokenRepository;

import java.util.UUID;
import java.util.function.Function;

public interface JwtService {
    String generateAccessToken(UserDTO user);
    String generateAccessToken(UUID uuid);
    String getNewUserRefreshToken(User user);
    String getNewModeratorRefreshToken(Moderator moderator);
    <RT extends RefreshToken<RT>, ID> String verifyAndUpdateRefreshToken(RefreshTokenRepository<RT, ID> repository, String token);
    <RT extends RefreshToken<RT>, ID> boolean deleteRefreshToken(RefreshTokenRepository<RT, ID> repository, String token);
    Boolean validateAccessToken(String token, User user);
    Boolean validateAccessToken(String token, String uuid);
    String getSubject(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimResolver);
}
