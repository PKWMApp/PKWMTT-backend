package org.pkwmtt.security.auth;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.security.auth.dto.UserRequestDTO;
import org.pkwmtt.security.token.dto.UserDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;

    @Override
    public UserDTO authenticateUser(String email) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                email,
                null
        ));

        if (!authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("Invalid credentials");
        }

        return (UserDTO) authentication.getPrincipal();
    }
}
