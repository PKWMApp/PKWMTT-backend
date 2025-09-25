package org.pkwmtt.security.moderator;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moderator")
@RequiredArgsConstructor
public class ModeratorController {

    private final ModeratorService moderatorService;

    @GetMapping("/authenticate")
    public ResponseEntity<String> authenticate (@RequestHeader(name = "Authorization") String password) {
        return ResponseEntity.ok(moderatorService.generateTokenForModerator(password));
    }

    @PostMapping("/users")
    public ResponseEntity<Void> addUser (@RequestBody UserDto userDto) {
        moderatorService.addUser(userDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(moderatorService.getUsers());
    }
}
