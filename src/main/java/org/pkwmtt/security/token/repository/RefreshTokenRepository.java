package org.pkwmtt.security.token.repository;

import org.pkwmtt.security.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    long deleteByToken(String token);

    default Boolean deleteTokenAsBoolean(String token){
        return deleteByToken(token) > 0;
    }
}
