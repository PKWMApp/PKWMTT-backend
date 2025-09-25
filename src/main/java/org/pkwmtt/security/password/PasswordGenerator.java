package org.pkwmtt.security.password;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.function.Function;

public interface PasswordGenerator {
    <T, ID> String generateUniquePassword(JpaRepository<T, ID> repository, Function<T, String> passwordExtractor, int length);
}
