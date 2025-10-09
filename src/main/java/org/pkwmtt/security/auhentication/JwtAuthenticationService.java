package org.pkwmtt.security.auhentication;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.InvalidRefreshTokenException;
import org.pkwmtt.security.auhentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.auhentication.dto.RefreshRequestDto;
import org.pkwmtt.security.token.JwtService;
import org.pkwmtt.security.token.dto.UserDTO;
import org.pkwmtt.security.token.entity.RefreshToken;
import org.pkwmtt.security.token.entity.UserRefreshToken;
import org.pkwmtt.security.token.repository.UserRefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {
    private final JwtService jwtService;
    private final UserRefreshTokenRepository userRefreshTokenRepository;


    public JwtAuthenticationDto refresh(RefreshRequestDto requestDto) throws JwtException {
        UserRefreshToken newUserRefreshToken = jwtService.verifyAndUpdateRefreshToken(userRefreshTokenRepository, requestDto.getRefreshToken());
        return JwtAuthenticationDto.builder()
                .refreshToken(newUserRefreshToken.getToken())
                .accessToken(jwtService.generateAccessToken(new UserDTO(newUserRefreshToken.getUser())))
                .build();
    }

    public void logout(RefreshRequestDto requestDto) {
        if(!jwtService.deleteRefreshToken(userRefreshTokenRepository, requestDto.getRefreshToken()))
            throw new InvalidRefreshTokenException();
    }
}
