package org.pkwmtt.security.token.entity;

import java.time.LocalDateTime;

public interface RefreshToken {
    String getToken();
    LocalDateTime getExpires();
}
