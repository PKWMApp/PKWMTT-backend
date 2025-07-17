package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.pkwmtt.enums.Role;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userId;

    @ManyToOne
    @JoinColumn(name = "general_group_id")
    private GeneralGroup generalGroup;

    private String email;

    private boolean isActive;

    private Role role;

    @OneToOne(mappedBy = "user")
    private OTPCode otpCode;
}
