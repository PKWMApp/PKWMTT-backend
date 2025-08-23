package org.pkwmtt.otp;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pkwmtt/api/v1/representative")
@RequiredArgsConstructor
public class OTPController {
    private final OTPService service;
    
    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate (@RequestParam(name = "c") String code) {
        
        return ResponseEntity.ok(service.generateTokenForRepresentative(code));
    }
    
}
