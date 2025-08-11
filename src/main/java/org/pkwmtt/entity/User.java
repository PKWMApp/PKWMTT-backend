package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.pkwmtt.enums.Role;

@Entity
@Data
@Table(name = "`users`")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "general_group_id")
    private GeneralGroup generalGroup;

    private String email;

    @Column(name = "is_active")
    private boolean isActive;

    private Role role;

    @OneToOne(mappedBy = "user")
    private OTPCode otpCode;
}
