package org.pkwmtt.security.moderator;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModeratorRepository extends JpaRepository<Moderator, UUID> {
}

