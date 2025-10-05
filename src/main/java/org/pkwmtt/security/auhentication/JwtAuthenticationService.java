package org.pkwmtt.security.auhentication;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.InvalidRefreshTokenException;
import org.pkwmtt.security.auhentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.auhentication.dto.RefreshRequestDto;
import org.pkwmtt.security.token.JwtService;
import org.pkwmtt.security.token.dto.UserDTO;
import org.pkwmtt.security.token.entity.RefreshToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtAuthenticationService {
    private final JwtService jwtService;


    public JwtAuthenticationDto refresh(RefreshRequestDto requestDto) throws JwtException {
        RefreshToken newRefreshToken = jwtService.verifyAndUpdateRefreshToken(requestDto.getRefreshToken());
        return JwtAuthenticationDto.builder()
                .refreshToken(newRefreshToken.getToken())
                .accessToken(jwtService.generateAccessToken(new UserDTO(newRefreshToken.getUser())))
                .build();
    }

    public void logout(RefreshRequestDto requestDto) {
        if(!jwtService.deleteRefreshToken(requestDto.getRefreshToken()))
            throw new InvalidRefreshTokenException();
    }
}
