package org.pkwmtt.security.apiKey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@MappedSuperclass
@NoArgsConstructor
@Getter
public abstract class BaseApiKeyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer key_id;
    
    @Column(nullable = false)
    protected String value;
    
    @Column(nullable = false)
    protected String description;
    
    public BaseApiKeyEntity (String value, String description) {
        this.value = value;
        this.description = description;
    }
}
