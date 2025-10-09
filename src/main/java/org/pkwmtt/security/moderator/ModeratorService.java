package org.pkwmtt.security.moderator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.examCalendar.repository.UserRepository;
import org.pkwmtt.exceptions.InvalidRefreshTokenException;
import org.pkwmtt.security.auhentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.auhentication.dto.RefreshRequestDto;
import org.pkwmtt.security.token.JwtService;
import org.pkwmtt.security.token.dto.UserDTO;
import org.pkwmtt.security.token.entity.ModeratorRefreshToken;
import org.pkwmtt.security.token.entity.UserRefreshToken;
import org.pkwmtt.security.token.repository.ModeratorRefreshTokenRepository;
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
    private final ModeratorRefreshTokenRepository moderatorRefreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public JwtAuthenticationDto generateTokenForModerator(String password) {
        Moderator moderator = moderatorRepository.findAll()
                .stream()
                .filter(m -> passwordEncoder.matches(password, m.getPassword()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        return JwtAuthenticationDto.builder()
                .accessToken(jwtService.generateAccessToken(moderator.getModeratorId()))
                .refreshToken(jwtService.getNewModeratorRefreshToken(moderator))
                .build();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public JwtAuthenticationDto refresh(RefreshRequestDto requestDto) {
        ModeratorRefreshToken newModeratorRefreshToken = jwtService.verifyAndUpdateRefreshToken(moderatorRefreshTokenRepository, requestDto.getRefreshToken());
        return JwtAuthenticationDto.builder()
                .refreshToken(newModeratorRefreshToken.getToken())
                .accessToken(jwtService.generateAccessToken(newModeratorRefreshToken.getModerator().getModeratorId()))
                .build();
    }

    public void logout(RefreshRequestDto requestDto) {
        if(!jwtService.deleteRefreshToken(moderatorRefreshTokenRepository, requestDto.getRefreshToken()))
            throw new InvalidRefreshTokenException();
    }
}
