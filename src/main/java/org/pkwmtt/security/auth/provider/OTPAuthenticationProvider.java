package org.pkwmtt.security.auth.provider;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.entity.User;
import org.pkwmtt.repository.UserRepository;
import org.pkwmtt.security.token.dto.UserDTO;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class OTPAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String otpCode = authentication.getCredentials().toString();

        // Fetch user from DB
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        // Wrap role in a list to support multiple roles in the future
        List<SimpleGrantedAuthority> authorities = Stream.of(user.getRole())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();

        // Validate critical user fields before OTP check
        if(!isValidForAuthentication(user)){
            throw new BadCredentialsException(
                    "Invalid User Credentials. Please contact the administrator."
            );
        }

        // TODO: integrate with real OTP service
        boolean OTP_SERVICE_AUTH_RESULT = true;

        if(OTP_SERVICE_AUTH_RESULT){
            UserDTO userMapped = new UserDTO(user);
            return new UsernamePasswordAuthenticationToken(userMapped, otpCode, authorities);
        } else {
            throw new BadCredentialsException("Invalid OTP");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    /**
     * Validates user data before authentication.
     * Returns true if user has email, role, group, and is active.
     */
    private boolean isValidForAuthentication(User user) {
        return user.getEmail() != null &&
                user.getRole() != null &&
                user.getGeneralGroup() != null &&
                user.isActive();
    }
}
