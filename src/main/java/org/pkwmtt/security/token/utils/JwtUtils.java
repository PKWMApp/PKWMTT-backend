package org.pkwmtt.security.token.utils;

import lombok.Getter;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtUtils {
    private Environment environment;
    private final String SECRET;
    private final long EXPIRATION_MS = 1000L * 60 * 60 * 24 * 30 * 6;

    public JwtUtils(Environment environment) {
        this.SECRET = environment.getProperty("JWT_SECRET_KEY");
        if(this.SECRET == null) {
            throw new RuntimeException("JWT_SECRET_KEY not found in environment variables");
        }
    }
}
