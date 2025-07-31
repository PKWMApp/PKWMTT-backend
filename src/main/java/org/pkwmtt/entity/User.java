package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.pkwmtt.enums.Role;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "general_group_id")
    private GeneralGroup generalGroup;

    private String email;

    private boolean isActive;

    private Role role;

    @OneToOne(mappedBy = "user")
    private OTPCode otpCode;

    public User() {

    }
}
