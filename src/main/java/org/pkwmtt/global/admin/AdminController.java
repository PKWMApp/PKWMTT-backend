package org.pkwmtt.global.admin;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.security.apiKey.ApiKeyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final ApiKeyService service;
    
    @GetMapping("")
    public String adminPanel(){
        return "ADMIN";
    }
    
    @GetMapping("/api/key/generate")
    public String generateApiKey (@RequestParam(name = "d") String description) {
        return service.generateApiKey(description);
    }
}
