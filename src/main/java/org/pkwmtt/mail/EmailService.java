package org.pkwmtt.mail;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.MailServiceNotAvailableException;
import org.pkwmtt.mail.config.MailConfig;
import org.pkwmtt.mail.dto.MailDTO;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {
    private final Environment environment;
    
    private final JavaMailSender mailSender;
    
    private String hostEmail;
    
    @PostConstruct
    private void assignProperties () {
        hostEmail = environment.getProperty("spring.mail.username");
    }
    
    public void send (MailDTO mail) throws MessagingException, MailServiceNotAvailableException {
        if (!MailConfig.isEnabled()) {
            throw new MailServiceNotAvailableException();
        }
        
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(hostEmail);
        helper.setTo(mail.getRecipient());
        helper.setText(mail.getDescription(), true);
        helper.setSubject(mail.getTitle());
        
        mailSender.send(message);
        
    }
}
