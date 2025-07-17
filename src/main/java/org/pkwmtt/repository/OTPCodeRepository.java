package org.pkwmtt.repository;

import org.pkwmtt.entity.OTPCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OTPCodeRepository extends JpaRepository<OTPCode, Long> {
}
