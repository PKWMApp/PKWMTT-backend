package org.pkwmtt.security.moderator.controller;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.entity.User;
import org.pkwmtt.security.moderator.dto.AuthDto;
import org.pkwmtt.security.moderator.ModeratorService;
import org.pkwmtt.security.moderator.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/moderator")
@RequiredArgsConstructor
public class ModeratorController {

    private final ModeratorService moderatorService;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate (@RequestBody AuthDto auth) {
        return ResponseEntity.ok(moderatorService.generateTokenForModerator(auth.getPassword()));
    }

    @PostMapping("/users")
    public ResponseEntity<Void> addUser (@RequestBody UserDto userDto) {
        moderatorService.addUser(userDto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/multiple-users")
    public ResponseEntity<Void> addMultipleUser (@RequestBody List<UserDto> userDto) {
        moderatorService.addUser(userDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(moderatorService.getUsers());
    }
}
