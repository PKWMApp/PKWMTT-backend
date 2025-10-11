package org.pkwmtt.security.moderator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.examCalendar.repository.UserRepository;
import org.pkwmtt.exceptions.InvalidRefreshTokenException;
import org.pkwmtt.security.auhentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.auhentication.dto.RefreshRequestDto;
import org.pkwmtt.security.token.JwtService;
import org.pkwmtt.security.token.JwtServiceImpl;
import org.pkwmtt.security.token.entity.ModeratorRefreshToken;
import org.pkwmtt.security.token.repository.ModeratorRefreshTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

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

        ModeratorRefreshToken moderatorRefreshToken = findRefreshToken(requestDto.getRefreshToken());

        JwtServiceImpl.validateRefreshToken(moderatorRefreshToken);

        String tokenHash = JwtServiceImpl.generateRefreshToken();

        moderatorRefreshToken.updateToken(passwordEncoder.encode(tokenHash));
        moderatorRefreshTokenRepository.save(moderatorRefreshToken);

        UUID id = moderatorRefreshToken.getModerator().getModeratorId();

        return JwtAuthenticationDto.builder()
                .refreshToken(tokenHash)
                .accessToken(jwtService.generateAccessToken(id))
                .build();
    }

    public void logout(RefreshRequestDto requestDto) {
        if(!jwtService.deleteRefreshToken(moderatorRefreshTokenRepository, requestDto.getRefreshToken()))
            throw new InvalidRefreshTokenException();
    }

    private ModeratorRefreshToken findRefreshToken(String token)
            throws InvalidRefreshTokenException {
        List<ModeratorRefreshToken> refreshTokens = moderatorRefreshTokenRepository.findAll();
        return refreshTokens.stream()
                .filter(rt -> passwordEncoder.matches(token, rt.getToken()))
                .findFirst().orElseThrow(InvalidRefreshTokenException::new);
    }
}
