package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.OTPCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPCodeRepository extends JpaRepository<OTPCode, Integer> {
}