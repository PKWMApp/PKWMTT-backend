package org.pkwmtt.security.token.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pkwmtt.examCalendar.entity.User;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer token_id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private boolean enabled;

    private LocalDateTime created;

    private LocalDateTime expires;

    public RefreshToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.enabled = true;
        this.created = LocalDateTime.now();
        this.expires = LocalDateTime.now().plusMonths(6);
    }

    public RefreshToken update(String token) {
        this.token = token;
        this.created = LocalDateTime.now();
        this.expires = LocalDateTime.now().plusMonths(6);
        return this;
    }
}
