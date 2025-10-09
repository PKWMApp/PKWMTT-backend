package org.pkwmtt.security.token.repository;

import org.pkwmtt.security.token.entity.UserRefreshToken;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRefreshTokenRepository extends RefreshTokenRepository<UserRefreshToken, Integer>{


}
