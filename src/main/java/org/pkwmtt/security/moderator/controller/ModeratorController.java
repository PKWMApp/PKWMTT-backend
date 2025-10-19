package org.pkwmtt.security.moderator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.Representative;
import org.pkwmtt.studentCodes.StudentCodeService;
import org.pkwmtt.studentCodes.dto.StudentCodeRequest;
import org.pkwmtt.security.auhentication.dto.JwtAuthenticationDto;
import org.pkwmtt.security.auhentication.dto.RefreshRequestDto;
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
    private final StudentCodeService studentCodeService;
    
    @PostMapping("/authenticate")
    public ResponseEntity<JwtAuthenticationDto> authenticate (@RequestBody AuthDto auth) {
        return ResponseEntity.ok(moderatorService.generateTokenForModerator(auth.getPassword()));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationDto> refresh (@RequestBody RefreshRequestDto requestDto) {
        return ResponseEntity.ok(moderatorService.refresh(requestDto));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout (@RequestBody RefreshRequestDto requestDto) {
        moderatorService.logout(requestDto);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/users")
    public ResponseEntity<Void> addUser (@RequestBody StudentCodeRequest studentCodeRequest)
      throws JsonProcessingException {
        studentCodeService.sendStudentCode(studentCodeRequest);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/multiple-users")
    public ResponseEntity<?> addMultipleUser (@RequestBody List<StudentCodeRequest> studentCodeRequests) {
        var failures = studentCodeService.sendStudentCodes(studentCodeRequests);
        if (failures == null || failures.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(207).body(failures);
    }
    
    @GetMapping("/users")
    public ResponseEntity<List<Representative>> getAllUsers () {
        return ResponseEntity.ok(moderatorService.getUsers());
    }
}
