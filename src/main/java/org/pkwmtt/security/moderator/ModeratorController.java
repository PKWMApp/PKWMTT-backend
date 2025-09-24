package org.pkwmtt.security.moderator;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/moderator")
@RequiredArgsConstructor
public class ModeratorController {

    private final ModeratorService moderatorService;

    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticate (@RequestHeader(name = "Authorization") String password) {
        return ResponseEntity.ok(moderatorService.generateTokenForModerator(password));
    }
}
