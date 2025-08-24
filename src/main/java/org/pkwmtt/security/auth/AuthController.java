package org.pkwmtt.security.auth;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.security.auth.dto.UserRequestDTO;
import org.pkwmtt.security.token.JwtServiceImpl;
import org.pkwmtt.security.token.dto.UserDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pkwmtt/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtServiceImpl jwtServiceImpl;

    @PostMapping("/authenticate")
    public String authenticate(@RequestBody UserRequestDTO requestUser) {
        UserDTO user = authService.authenticateUser(requestUser);
        return jwtServiceImpl.generateToken(user);
    }
}
