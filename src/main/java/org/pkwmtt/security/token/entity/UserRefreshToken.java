package org.pkwmtt.security.token.entity;

import jakarta.persistence.*;
import lombok.*;
import org.pkwmtt.examCalendar.entity.Representative;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "user_refresh_token")
public class UserRefreshToken implements RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer token_id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "representative_id")
    private Representative representative;

    private LocalDateTime created;

    private LocalDateTime expires;

    public UserRefreshToken(String token, Representative representative) {
        this.token = token;
        this.representative = representative;
        this.created = LocalDateTime.now();
        this.expires = LocalDateTime.now().plusMonths(6);
    }

    public void updateToken(String token) {
        this.token = token;
        this.created = LocalDateTime.now();
    }
}
