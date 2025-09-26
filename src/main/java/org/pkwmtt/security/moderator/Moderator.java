package org.pkwmtt.security.moderator;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "moderators")
@Getter
@NoArgsConstructor
public class Moderator {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID moderatorId;
    private String password;
    private String role;

    public Moderator(String encryptedPassword) {
        password = encryptedPassword;
        role = "MODERATOR";
    }
}
