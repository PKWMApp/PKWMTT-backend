package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.pkwmtt.enums.Role;

@Entity
@Data
@Table(name = "`users`")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne
    @JoinColumn(name = "general_group_id", nullable = false)
    private GeneralGroup generalGroup;

    @Column(nullable = false)
    private String email;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
