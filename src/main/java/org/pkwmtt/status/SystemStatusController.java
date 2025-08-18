package org.pkwmtt.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pkwmtt/system/status")
@RequiredArgsConstructor
public class SystemStatusController {
    private final SystemStatusCheckerService service;
    
    @GetMapping
    public ResponseEntity<String> getSystemStatus () {
        return ResponseEntity.ok(service.getStatus());
    }
    
}
