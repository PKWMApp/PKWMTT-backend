package org.pkwmtt.security.password;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SimplePasswordGenerator implements PasswordGenerator {

    private final PasswordEncoder passwordEncoder;

    private final String availableCharacters = "ABCDEFGHIJKLMNOPQRSTUWXYZ0123456789";
    private final SecureRandom random = new SecureRandom();

    public <T, ID> String generateUniquePassword(JpaRepository<T, ID> repository, Function<T, String> passwordExtractor, int length) {
        if (length < 0)
            throw new IllegalArgumentException("length must be a positive integer");
        StringBuilder code = new StringBuilder();
        do {
            code.setLength(0);
            for (int i = 0; i < length; i++) {
                code.append(availableCharacters.charAt(random.nextInt(availableCharacters.length())));
            }
        } while (repository.findAll().stream()
                .map(passwordExtractor)
                .anyMatch(encrypted -> passwordEncoder.matches(code, encrypted))
        );

        return code.toString();
    }
}
