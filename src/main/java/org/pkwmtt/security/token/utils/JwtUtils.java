package org.pkwmtt.security.token.utils;

import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtUtils {
    // Secret key used for signing JWTs. If the environment variable JWT_SECRET_KEY
    // is not set, a default value "TEST_SECRET" is used. This allows the application
    // to start without a real secret, e.g., for local development or tests.
    private final String secret;
    private final long expirationMs = 1000L * 60 * 60 * 24 * 30 * 6;

    public JwtUtils(Environment environment) {
        // Get the secret key from environment variables, or fallback to "TEST_SECRET"
        this.secret = environment.getProperty("JWT_SECRET_KEY", "TEST_SECRET");
    }
}
