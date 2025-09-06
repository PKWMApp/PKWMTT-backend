package org.pkwmtt.global.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
    @Operation(summary = "Admin endpoint", parameters = {@Parameter(name = "X-ADMIN-KEY", in = ParameterIn.HEADER, required = true, description = "Admin access key")})
    public String adminPanel () {
        return "ADMIN";
    }
    
    @GetMapping("/api/key/generate")
    @Operation(summary = "Admin endpoint", parameters = {@Parameter(name = "X-ADMIN-KEY", in = ParameterIn.HEADER, required = true, description = "Admin access key")})
    public String generateApiKey (@RequestParam(name = "d") String description) {
        return service.generateApiKey(description);
    }
}
