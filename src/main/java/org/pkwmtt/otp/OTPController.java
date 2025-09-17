package org.pkwmtt.otp;


import com.mysql.cj.exceptions.WrongArgumentException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.*;
import org.pkwmtt.otp.dto.OTPRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${apiPrefix}/representatives")
@RequiredArgsConstructor
public class OTPController {
    private final OTPService service;
    
    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticate (@RequestParam(name = "c") String code)
      throws OTPCodeNotFoundException, WrongOTPFormatException, UserNotFoundException {
        return ResponseEntity.ok(service.generateTokenForRepresentative(code));
    }
    
    @PostMapping("/codes/generate")
    public ResponseEntity<Void> generateCodes (@RequestBody List<OTPRequest> request)
      throws MailCouldNotBeSendException, WrongArgumentException, SpecifiedGeneralGroupDoesntExistsException, IllegalArgumentException {
        service.sendOTPCodesForManyGroups(request);
        return ResponseEntity.ok().build();
    }
    
}
