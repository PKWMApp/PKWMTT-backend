package org.pkwmtt.examCalendar.repository;

import org.pkwmtt.examCalendar.entity.GeneralGroup;
import org.pkwmtt.examCalendar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail (String email);
    
    Optional<User> findByGeneralGroup (GeneralGroup generalGroup);

    @Query("SELECT g.name FROM User u LEFT JOIN u.generalGroup g where u.email = :email")
    Optional<String> findGroupByUserEmail (@Param("email") String userEmail);
}