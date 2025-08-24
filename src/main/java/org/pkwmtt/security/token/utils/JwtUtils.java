package org.pkwmtt.security.token.utils;

import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtUtils {
    private final String secret;
    private final long expirationMs = 1000L * 60 * 60 * 24 * 30 * 6;

    public JwtUtils(Environment environment) {
        this.secret = environment.getProperty("JWT_SECRET_KEY");
        if(this.secret == null) {
            throw new IllegalStateException("JWT_SECRET_KEY not found in environment variables");
        }
    }
}
