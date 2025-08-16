package org.pkwmtt.mail;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.MailServiceNotAvailableException;
import org.pkwmtt.exceptions.dto.ErrorResponseDTO;
import org.pkwmtt.mail.dto.MailDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class EmailTempController {
    
    private final EmailService service;
    
    @PostMapping
    public void sendMail (@RequestParam(name = "r") String recipientEmailAddress)
      throws MessagingException, MailServiceNotAvailableException {
        service.send(new MailDTO()
                       .setRecipient(recipientEmailAddress)
                       .setDescription("TEST")
                       .setTitle("TEST"));
    }
    
    @ExceptionHandler(MailServiceNotAvailableException.class)
    public ResponseEntity<ErrorResponseDTO> handle (Exception e) {
        return new ResponseEntity<>(
          new ErrorResponseDTO(e.getMessage()),
                                    HttpStatus.SERVICE_UNAVAILABLE
        );
    }
}
