package org.pkwmtt.otp;


import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.MailCouldNotBeSendException;
import org.pkwmtt.exceptions.OTPCodeNotFoundException;
import org.pkwmtt.exceptions.UserNotFoundException;
import org.pkwmtt.exceptions.WrongOTPFormatException;
import org.pkwmtt.otp.dto.OTPRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pkwmtt/api/v1/representatives")
@RequiredArgsConstructor
public class OTPController {
    private final OTPService service;
    
    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticate (@RequestParam(name = "c") String code)
      throws OTPCodeNotFoundException, WrongOTPFormatException, UserNotFoundException {
        return ResponseEntity.ok(service.generateTokenForRepresentative(code));
    }
    
    @PostMapping("/codes/generate")
    public ResponseEntity<Void> generateCodes (@RequestBody List<OTPRequest> request) throws MailCouldNotBeSendException {
        service.sendOTPCodes(request);
        return ResponseEntity.ok().build();
    }
    
}
