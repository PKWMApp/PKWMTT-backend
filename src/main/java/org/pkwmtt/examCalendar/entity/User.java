package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pkwmtt.enums.Role;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`users`")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer user_id;

    @ManyToOne
    @JoinColumn(name = "general_group_id")
    private GeneralGroup general_group;

    private String email;

    private boolean is_active;

    private Role role;

    @OneToOne(mappedBy = "user")
    private OTPCode otp_code;
}
