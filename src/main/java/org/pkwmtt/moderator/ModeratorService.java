package org.pkwmtt.moderator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.Representative;
import org.pkwmtt.examCalendar.repository.RepresentativeRepository;
import org.pkwmtt.exceptions.InvalidRefreshTokenException;
import org.pkwmtt.security.authentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.authentication.dto.RefreshRequestDto;
import org.pkwmtt.security.jwt.JwtService;
import org.pkwmtt.security.jwt.refreshToken.entity.ModeratorRefreshToken;
import org.pkwmtt.security.jwt.refreshToken.entity.RefreshToken;
import org.pkwmtt.security.jwt.refreshToken.repository.ModeratorRefreshTokenRepository;
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

    private final RepresentativeRepository representativeRepository;

    public JwtAuthenticationDto generateTokenForModerator(String password) {
        Moderator moderator = moderatorRepository.findAll()
                .stream()
                .filter(m -> passwordEncoder.matches(password, m.getPassword()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        return JwtAuthenticationDto.builder()
                .accessToken(jwtService.generateAccessToken(moderator.getModeratorId()))
                .refreshToken(getNewModeratorRefreshToken(moderator))
                .build();
    }

    public List<Representative> getUsers() {
        return representativeRepository.findAll();
    }


    public JwtAuthenticationDto refresh(RefreshRequestDto requestDto) {

        ModeratorRefreshToken moderatorRefreshToken = findRefreshToken(requestDto.getRefreshToken());
        JwtService.validateRefreshToken(moderatorRefreshToken);

        String tokenHash = JwtService.generateRefreshToken();

        moderatorRefreshToken.updateToken(passwordEncoder.encode(tokenHash));
        moderatorRefreshTokenRepository.save(moderatorRefreshToken);

        UUID id = moderatorRefreshToken.getModerator().getModeratorId();

        return JwtAuthenticationDto.builder()
                .refreshToken(tokenHash)
                .accessToken(jwtService.generateAccessToken(id))
                .build();
    }

    public void logout(RefreshRequestDto requestDto) {
        RefreshToken refreshToken = findRefreshToken(requestDto.getRefreshToken());
        if(!moderatorRefreshTokenRepository.deleteTokenAsBoolean(refreshToken.getToken()))
            throw new InvalidRefreshTokenException();
    }

    private String getNewModeratorRefreshToken(Moderator moderator) {
        String token = JwtService.generateRefreshToken();
        moderatorRefreshTokenRepository.save(new ModeratorRefreshToken(passwordEncoder.encode(token), moderator));
        return token;
    }

    private ModeratorRefreshToken findRefreshToken(String token)
            throws InvalidRefreshTokenException {
        List<ModeratorRefreshToken> refreshTokens = moderatorRefreshTokenRepository.findAll();
        return refreshTokens.stream()
                .filter(rt -> passwordEncoder.matches(token, rt.getToken()))
                .findFirst().orElseThrow(InvalidRefreshTokenException::new);
    }
}
