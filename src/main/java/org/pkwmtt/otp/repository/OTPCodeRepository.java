package org.pkwmtt.otp.repository;

import org.pkwmtt.examCalendar.entity.OTPCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OTPCodeRepository
  extends JpaRepository<OTPCode, Integer> {
    Optional<OTPCode> findByCode (String code);
}