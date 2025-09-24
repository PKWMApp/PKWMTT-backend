package org.pkwmtt.security.moderator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.security.token.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public String generateTokenForModerator(String password) {
        return moderatorRepository.findAll()
                .stream()
                .filter(m -> passwordEncoder.matches(m.getPassword(), password))
                .findFirst()
                .map(m -> jwtService.generateToken(m.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }
}
