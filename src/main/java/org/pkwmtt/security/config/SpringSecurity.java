package org.pkwmtt.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pkwmtt.security.token.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SpringSecurity {

    private final JwtFilter jwtFilter;
    
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
                  .requestMatchers("/moderator/authenticate").permitAll()
                  .requestMatchers("/moderator/**").hasAuthority("ROLE_MODERATOR")
                  .requestMatchers("/**").permitAll()
                  .anyRequest().authenticated()
          )
          .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        log.info("Configuring Success...");
        return http.build();
    }
}
