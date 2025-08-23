package org.pkwmtt.otp;


import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<String> authenticate (@RequestParam(name = "c") String code) {
        return ResponseEntity.ok(service.generateTokenForRepresentative(code));
    }
    
    @PostMapping("/codes/generate")
    public ResponseEntity<Void> generateCodes (@RequestBody List<OTPRequest> request) {
        service.sendOTPCodes(request);
        return ResponseEntity.ok().build();
    }
    
}
