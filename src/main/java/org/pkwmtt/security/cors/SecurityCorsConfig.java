package org.pkwmtt.security.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * global CORS configuration
 * Works only with ENABLED security
 * Remember to change localhost to real address
 */
@Configuration
public class SecurityCorsConfig {

    /**
     * @return UrlBasedCorsConfigurationSource with CORS configuration for spring security
     * added to SecurityFilterChain by .cors(withDefaults())
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://pkwmapp.pl"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);   //???

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/pkmwtt/api/**", config);

        return source;
    }
}
