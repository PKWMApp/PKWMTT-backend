package org.pkwmtt.examCalendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "otp_codes")
public class OTPCode {
    
    public OTPCode (String code, GeneralGroup generalGroup) {
        this.code = code;
        this.expire = LocalDateTime.now().plusDays(1);
        this.generalGroup = generalGroup;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_code_id")
    private Integer otpCodeId;
    
    @Column(nullable = false)
    private String code;
    
    @Column(nullable = false)
    private LocalDateTime expire;
    
    @OneToOne
    @JoinColumn(name = "general_group_id", nullable = false)
    private GeneralGroup generalGroup;
}
