package org.pkwmtt.security.apiKey.repository;

import org.pkwmtt.security.apiKey.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Integer> {
    boolean existsApiKeyByValue (String value);
}
