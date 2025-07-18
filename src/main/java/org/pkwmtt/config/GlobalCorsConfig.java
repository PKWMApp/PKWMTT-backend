package org.pkwmtt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * global CORS configuration
 * Works only with DISABLED security, after enabling security should be removed
 * Remember to change localhost to real address
 */
@Configuration
public class GlobalCorsConfig{

    /**
     * @return WebMvcConfigurer with CORS configuration for specific paths, methods and origins
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/pkmwtt/api/**")
//                        TODO: change host
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
//                required for authorization and cookies, default false
                        /*.allowCredentials(true)*/;

            }
        };
    }

}
