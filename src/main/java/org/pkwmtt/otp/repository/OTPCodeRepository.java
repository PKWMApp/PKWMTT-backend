package org.pkwmtt.otp.repository;

import jakarta.transaction.Transactional;
import org.pkwmtt.examCalendar.entity.GeneralGroup;
import org.pkwmtt.examCalendar.entity.OTPCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPCodeRepository extends JpaRepository<OTPCode, Integer> {
    Optional<OTPCode> findByCode (String code);
    
    @Transactional
    void deleteByCode (String code);
    
    boolean existsOTPCodeByGeneralGroup (GeneralGroup generalGroup);
    
}