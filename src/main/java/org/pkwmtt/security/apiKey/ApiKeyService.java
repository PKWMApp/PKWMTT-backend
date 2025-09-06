package org.pkwmtt.security.apiKey;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.IncorrectApiKeyValue;
import org.pkwmtt.security.apiKey.entity.ApiKey;
import org.pkwmtt.security.apiKey.repository.ApiKeyRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    
    private final ApiKeyRepository repository;
    
    public String generateApiKey (String description) {
        String value = UUID.randomUUID().toString();
        repository.save(new ApiKey(value, description));
        return value;
    }
    
    public void validateApiKey (String value) throws IncorrectApiKeyValue {
        try {
            UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IncorrectApiKeyValue();
        }
        
        if (!repository.existsApiKeyByValue(value)) {
            throw new IncorrectApiKeyValue();
        }
    }
}
