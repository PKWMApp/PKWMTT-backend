package org.pkwmtt.security.apiKey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "api_keys")
@Getter
@NoArgsConstructor
public class ApiKey extends BaseApiKeyEntity {
    
    public ApiKey (String value, String description) {
        super(value, description);
    }
    
}