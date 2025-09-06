package org.pkwmtt.security.apiKey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "api_keys")
@NoArgsConstructor
@Getter
public class ApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer key_id;
    
    @Column(nullable = false)
    private String value;
    
    @Column(nullable = false)
    private String description;
    
    public ApiKey (String value, String description) {
        this.value = value;
        this.description = description;
    }
}
