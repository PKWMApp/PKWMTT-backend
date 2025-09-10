package org.pkwmtt.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@Slf4j
@Configuration
public class SpringSecurity {
    
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {
        log.info("Configuring Security Filter Chain...");
        http
          .cors(withDefaults())
          .csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests(auth -> auth
                  .requestMatchers(HttpMethod.POST , "/pkwmtt/api/v1/exams").authenticated()
                  .requestMatchers(HttpMethod.PUT , "/pkwmtt/api/v1/exams").authenticated()
                  .requestMatchers(HttpMethod.DELETE , "/pkwmtt/api/v1/exams").authenticated()
                  .requestMatchers("/**").permitAll()
                  .anyRequest().authenticated()
          )
          .sessionManagement(session -> session.sessionCreationPolicy(STATELESS));
        log.info("Configuring Success...");
        return http.build();
    }
}
