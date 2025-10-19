package org.pkwmtt.security.auhentication;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.exceptions.StudentCodeNotFoundException;
import org.pkwmtt.exceptions.UserNotFoundException;
import org.pkwmtt.exceptions.WrongStudentCodeFormatException;
import org.pkwmtt.studentCodes.StudentCodeService;
import org.pkwmtt.studentCodes.dto.StudentCodeDTO;
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
    private final StudentCodeService studentCodeService;

    @PostMapping("/authenticate")
    public ResponseEntity<JwtAuthenticationDto> authenticate (@RequestBody StudentCodeDTO code)
      throws StudentCodeNotFoundException, WrongStudentCodeFormatException, UserNotFoundException {
        return ResponseEntity.ok(studentCodeService.generateTokenForUser(code.getOtpCode()));
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