package org.pkwmtt.security.authentication.authenticationProvider;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.security.authentication.authenticationToken.JwtAuthenticationToken;
import org.pkwmtt.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.UUID;

@RequiredArgsConstructor
public class ModeratorAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
//        get data
        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        String token = auth.getCredentials();
        UUID subject = UUID.fromString(jwtService.getSubject(token));
        String role = jwtService.extractClaim(token, claims -> claims.get("role", String.class));

//        verify data
        jwtService.validateAccessToken(token, subject);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
