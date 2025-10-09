package org.pkwmtt.security.token.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository<RT, ID> extends JpaRepository<RT, ID> {

    long deleteByToken(String token);

    default Boolean deleteTokenAsBoolean(String token) {
        return deleteByToken(token) > 0;
    }
}
