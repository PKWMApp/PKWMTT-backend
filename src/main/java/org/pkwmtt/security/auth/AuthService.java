package org.pkwmtt.security.auth;

import org.pkwmtt.security.token.dto.UserDTO;

public interface AuthService {
    UserDTO authenticateUser(String email);
}
