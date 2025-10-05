package org.pkwmtt.security.auhentication;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.OTPCodeNotFoundException;
import org.pkwmtt.exceptions.UserNotFoundException;
import org.pkwmtt.exceptions.WrongOTPFormatException;
import org.pkwmtt.otp.OTPService;
import org.pkwmtt.security.auhentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.auhentication.dto.RefreshRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("${apiPrefix}/representatives")
@RequiredArgsConstructor
public class JwtAuthenticationController {

    private final JwtAuthenticationService jwtAuthenticationService;
    private final OTPService otpService;

    @PostMapping("/authenticate")
    public ResponseEntity<JwtAuthenticationDto> authenticate (@RequestParam(name = "c") String code)
            throws OTPCodeNotFoundException, WrongOTPFormatException, UserNotFoundException {
        return ResponseEntity.ok(otpService.generateTokenForRepresentative(code));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationDto> refresh(@RequestBody RefreshRequestDto requestDto){
        return ResponseEntity.ok(jwtAuthenticationService.refresh(requestDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshRequestDto requestDto){
        jwtAuthenticationService.logout(requestDto);
        return ResponseEntity.noContent().build();
    }



}