package org.pkwmtt.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
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
}
