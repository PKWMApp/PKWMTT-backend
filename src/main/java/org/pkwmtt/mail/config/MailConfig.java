package org.pkwmtt.mail.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class MailConfig {
    
    @Getter
    private static boolean enabled = true;
    
    private final Environment environment;
    
    private String username;
    private String password;
    
    @PostConstruct
    private void assignAndValidateProperties () {
        username = environment.getProperty("spring.mail.username");
        password = environment.getProperty("spring.mail.password");
        
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            enabled = false;
        }
    }
    
    @Bean
    public JavaMailSender javaMailSender () {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        
        if (!enabled) {
            return mailSender;
        }
        
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        return mailSender;
    }
    
}
