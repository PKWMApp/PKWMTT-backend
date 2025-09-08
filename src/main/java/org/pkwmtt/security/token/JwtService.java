package org.pkwmtt.security.token;

import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.security.token.dto.UserDTO;

import java.util.Optional;

public interface JwtService {
    String generateToken(UserDTO user);
    Boolean validateToken(String token, User user);
    String getUserEmailFromToken(String token);
}
