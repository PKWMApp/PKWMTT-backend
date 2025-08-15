package org.pkwmtt.mail;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.mail.dto.MailDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class EmailTempController {
    
    private final EmailService service;
    
    @PostMapping
    public void sendMail (@RequestParam(name = "r") String recipientEmailAddress)
      throws MessagingException {
        service.send(new MailDTO()
                       .setRecipient(recipientEmailAddress)
                       .setDescription("TEST")
                       .setTitle("TEST"));
    }
}
