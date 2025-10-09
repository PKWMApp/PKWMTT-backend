package org.pkwmtt.security.token.repository;

import org.pkwmtt.security.token.entity.ModeratorRefreshToken;
import org.springframework.stereotype.Repository;

@Repository
public interface ModeratorRefreshTokenRepository extends RefreshTokenRepository<ModeratorRefreshToken, Integer> {


}
