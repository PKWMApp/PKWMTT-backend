package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "otp_codes")
public class OTPCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "otp_code_id")
    private Integer otpCodeId;

    private String code;

    private LocalDateTime timestamp;

    private boolean used;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;
}
