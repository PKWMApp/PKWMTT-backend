package org.pkwmtt.security.moderator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.examCalendar.repository.UserRepository;
import org.pkwmtt.security.token.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public String generateTokenForModerator(String password) {
        return moderatorRepository.findAll()
                .stream()
                .filter(m -> passwordEncoder.matches(password, m.getPassword()))
                .findFirst()
                .map(m -> jwtService.generateAccessToken(m.getModeratorId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
