package org.pkwmtt.security.token.entity;

import java.time.LocalDateTime;

public interface RefreshToken<RT extends RefreshToken<RT>> {
    String getToken();
    LocalDateTime getExpires();
    boolean isEnabled();
    RT update(String newToken);
}
