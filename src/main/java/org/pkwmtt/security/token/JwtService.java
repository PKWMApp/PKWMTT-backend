package org.pkwmtt.security.token;

import org.pkwmtt.security.token.dto.UserDTO;

public interface JwtService {
    String generateToken(UserDTO user);
    Boolean validateToken(String token);
    String getUserIdFromToken(String token);
}
