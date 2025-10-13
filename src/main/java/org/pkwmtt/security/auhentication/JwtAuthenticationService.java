package org.pkwmtt.security.auhentication;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.exceptions.InvalidRefreshTokenException;
import org.pkwmtt.security.auhentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.auhentication.dto.RefreshRequestDto;
import org.pkwmtt.security.token.JwtService;
import org.pkwmtt.security.token.JwtServiceImpl;
import org.pkwmtt.security.token.dto.UserDTO;
import org.pkwmtt.security.token.entity.RefreshToken;
import org.pkwmtt.security.token.entity.UserRefreshToken;
import org.pkwmtt.security.token.repository.UserRefreshTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {
    private final JwtService jwtService;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final PasswordEncoder passwordEncoder;


    public JwtAuthenticationDto refresh(RefreshRequestDto requestDto) throws JwtException {

        UserRefreshToken userRefreshToken = findRefreshToken(requestDto.getRefreshToken());
        JwtServiceImpl.validateRefreshToken(userRefreshToken);

        String tokenHash = JwtServiceImpl.generateRefreshToken();

        userRefreshToken.updateToken(passwordEncoder.encode(tokenHash));
        userRefreshTokenRepository.save(userRefreshToken);

        User user = userRefreshToken.getUser();

        return JwtAuthenticationDto.builder()
                .refreshToken(tokenHash)
                .accessToken(jwtService.generateAccessToken(new UserDTO(user)))
                .build();
    }

    public void logout(RefreshRequestDto requestDto) {
        RefreshToken refreshToken = findRefreshToken(requestDto.getRefreshToken());
        if(!userRefreshTokenRepository.deleteTokenAsBoolean(refreshToken.getToken()))
            throw new InvalidRefreshTokenException();
    }

    public String getNewUserRefreshToken(User user) {
        String token = JwtServiceImpl.generateRefreshToken();
        userRefreshTokenRepository.save(new UserRefreshToken(passwordEncoder.encode(token), user));
        return token;
    }

    private UserRefreshToken findRefreshToken(String token)
            throws InvalidRefreshTokenException {
        List<UserRefreshToken> refreshTokens = userRefreshTokenRepository.findAll();
        return refreshTokens.stream()
                .filter(rt -> passwordEncoder.matches(token, rt.getToken()))
                .findFirst().orElseThrow(InvalidRefreshTokenException::new);
    }

}
