package org.pkwmtt.admin;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.examCalendar.enums.Role;
import org.pkwmtt.security.apiKey.ApiKeyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final ApiKeyService service;
    private final AdminService adminService;
    
    @GetMapping("")
    public String adminPanel () {
        return "ADMIN";
    }
    
    @PostMapping("/api/keys/generate")
    public String generateApiKey (@RequestParam(name = "d") String description,
                                  @RequestParam(name = "r") Role role) {
        return service.generateApiKey(description, role);
    }
    
    @GetMapping("/api/keys")
    public Map<String, String> getMapOfPublicApiKeys () {
        return service.getMapOfPublicApiKeys();
    }
    
    @PostMapping("/add-moderator")
    public ResponseEntity<String> addModerator () {
        return ResponseEntity.ok(adminService.addModerator());
    }
    
    
}
