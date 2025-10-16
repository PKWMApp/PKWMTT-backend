package org.pkwmtt.security.moderator;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "moderators")
public class Moderator {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "moderator_id")
    private UUID moderatorId;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String role;
    
    public Moderator(String password) {
        this.password = password;
        this.role = "MODERATOR";
    }
}

