package org.pkwmtt.security.auhentication.dto;

import lombok.Builder;

@Builder
public class JwtAuthenticationDto {
    private String accessToken;
    private String refreshToken;
}
