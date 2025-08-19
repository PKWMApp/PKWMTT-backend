package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
