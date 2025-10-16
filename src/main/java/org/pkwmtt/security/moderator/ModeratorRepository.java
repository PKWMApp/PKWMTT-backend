package org.pkwmtt.security.moderator;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ModeratorRepository extends JpaRepository<Moderator, byte[]> {
}

