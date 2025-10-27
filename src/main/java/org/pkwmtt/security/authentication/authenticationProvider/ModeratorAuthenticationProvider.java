package org.pkwmtt.security.authentication.authenticationProvider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.moderator.ModeratorRepository;
import org.pkwmtt.security.authentication.authenticationToken.JwtAuthenticationToken;
import org.pkwmtt.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class ModeratorAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;
    private final ModeratorRepository moderatorRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

//        get data
        JwtAuthenticationToken auth = (JwtAuthenticationToken) authentication;
        String token = auth.getCredentials();

//        verify token and data
        try{
            if(!Objects.equals(
                    jwtService.extractClaim(token, claims -> claims.get("role", String.class)),
                    "ROLE_MODERATOR")
            )
                return null;
        } catch (ExpiredJwtException e) {
            throw new CredentialsExpiredException("Token has expired");
        } catch (SignatureException e) {
            throw new BadCredentialsException("Invalid JWT token");
        } catch (JwtException e){
            throw new AuthenticationServiceException("Authentication failed");
        }

//        verify user
        UUID subject = UUID.fromString(jwtService.getSubject(token));
//        moderator was verified when token was generated
//        moderatorRepository.findById(subject).orElseThrow(() -> new UsernameNotFoundException("User not found"));

//        authentication successful
        GrantedAuthority role = new SimpleGrantedAuthority("ROLE_MODERATOR");
        return new JwtAuthenticationToken(subject, Collections.singletonList(role));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }
}