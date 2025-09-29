package org.pkwmtt.examCalendar.repository;

import jakarta.transaction.Transactional;
import org.pkwmtt.examCalendar.entity.GeneralGroup;
import org.pkwmtt.examCalendar.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail (String email);
    
    Optional<User> findByGeneralGroup (GeneralGroup generalGroup);

    @Query("SELECT g.name FROM User u LEFT JOIN u.generalGroup g where u.email = :email")
    
    @Transactional
    void deleteUserByEmail (String email);
    
}