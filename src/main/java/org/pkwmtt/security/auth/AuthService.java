package org.pkwmtt.security.auth;

import org.pkwmtt.security.auth.dto.UserRequestDTO;
import org.pkwmtt.security.token.dto.UserDTO;

public interface AuthService {
    UserDTO authenticateUser(UserRequestDTO requestUser);
}
