package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "otp_codes")
public class OTPCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_code_id")
    private Integer otpCodeId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expire;

    @OneToOne
    @JoinColumn(name = "`general_group_id`", nullable = false)
    private GeneralGroup generalGroup;
}
