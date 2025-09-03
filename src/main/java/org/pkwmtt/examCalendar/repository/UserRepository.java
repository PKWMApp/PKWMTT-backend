package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}