package org.pkwmtt.security.apiKey;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.exceptions.IncorrectApiKeyValue;
import org.pkwmtt.security.admin.entity.AdminKey;
import org.pkwmtt.security.admin.repository.AdminKeyRepository;
import org.pkwmtt.security.apiKey.entity.ApiKey;
import org.pkwmtt.security.apiKey.repository.ApiKeyRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApiKeyService {
    
    private final ApiKeyRepository apiKeyRepository;
    private final AdminKeyRepository adminKeyRepository;
    private final PasswordEncoder encoder;
    
    public String generateApiKey (String description, Role role) {
        String value = UUID.randomUUID().toString();
        if (role == Role.REPRESENTATIVE || role == Role.STUDENT || role == Role.ADMIN) {
            saveApiKey(value, description, role);
        }
        return value;
    }
    
    public void saveApiKey (String value, String description, Role role) {
        value = encoder.encode(value);
        if (role == Role.ADMIN) {
            adminKeyRepository.save(new AdminKey(value, description));
        } else {
            apiKeyRepository.save(new ApiKey(value, description));
        }
    }
    
    public void validateApiKey (String value, Role role) throws IncorrectApiKeyValue {
        if (existsInAdminKeyBase(value)) { // Admin can access all endpoint
            return;
        }
        
        if (role != Role.ADMIN && existsInPublicKeyBase(value)) {  // Normal user access
            return;
        }
        
        throw new IncorrectApiKeyValue();
    }
    
    public boolean existsInPublicKeyBase (String value) {
        return apiKeyRepository.findAll().stream()
          .map(ApiKey::getValue)
          .anyMatch(stored -> encoder.matches(value, stored));
    }
    
    public boolean existsInAdminKeyBase (String value) {
        return adminKeyRepository.findAll().stream()
          .map(AdminKey::getValue)
          .anyMatch(stored -> encoder.matches(value, stored));
    }
    
    public Map<String, String> getMapOfPublicApiKeys () {
        Map<String, String> objectMap = new HashMap<>();
        
        apiKeyRepository.findAll().forEach(item -> objectMap.put(item.getValue(), item.getDescription()));
        
        return objectMap;
    }
    
}
