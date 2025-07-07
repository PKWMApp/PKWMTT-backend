package org.pkwmtt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/hello")
public class MockController {
    @GetMapping
    public ResponseEntity<String> getHello() {
        return ResponseEntity.ok("Hello");
    }
}
