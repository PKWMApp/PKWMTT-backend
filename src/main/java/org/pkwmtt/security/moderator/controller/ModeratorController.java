package org.pkwmtt.security.moderator.controller;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.otp.OTPService;
import org.pkwmtt.otp.dto.OTPRequest;
import org.pkwmtt.security.moderator.ModeratorService;
import org.pkwmtt.security.moderator.dto.AuthDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moderator")
@RequiredArgsConstructor
public class ModeratorController {

    private final ModeratorService moderatorService;
    private final OTPService otpService;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate (@RequestBody AuthDto auth) {
        return ResponseEntity.ok(moderatorService.generateTokenForModerator(auth.getPassword()));
    }

    @PostMapping("/users")
    public ResponseEntity<Void> addUser (@RequestBody OTPRequest otpRequest) {
        otpService.sendOtpCode(otpRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/multiple-users")
    public ResponseEntity<Void> addMultipleUser (@RequestBody List<OTPRequest> otpRequests) {
        otpService.sendOTPCodesForManyGroups(otpRequests);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(moderatorService.getUsers());
    }
}
