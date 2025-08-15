package org.pkwmtt.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.mail.dto.MailDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {
    
    private final JavaMailSender mailSender;
    
    @Value("${EMAIL_USERNAME}")
    private String hostEmail;
    
    public void send (MailDTO mail) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom(hostEmail);
        helper.setTo(mail.getRecipient());
        helper.setText(mail.getDescription(), true);
        helper.setSubject(mail.getTitle());
        
        mailSender.send(message);
        
    }
}
