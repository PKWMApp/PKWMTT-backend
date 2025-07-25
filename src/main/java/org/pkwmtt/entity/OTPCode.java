package org.pkwmtt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "otp_codes")
public class OTPCode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long otpCodeId;

    private String code;

    private LocalDateTime timestamp;

    private boolean used;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public OTPCode() {

    }
}
