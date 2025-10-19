package org.pkwmtt.studentCodes;


import lombok.RequiredArgsConstructor;
import org.pkwmtt.studentCodes.dto.StudentCodeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${apiPrefix}/representatives")
@RequiredArgsConstructor
public class StudentCodeController {
    private final StudentCodeService service;
    
    @PostMapping("/codes/generate")
    public ResponseEntity<?> generateCodes (@RequestBody List<StudentCodeRequest> request) {
        var failures = service.sendStudentCodes(request);
        if (failures == null || failures.isEmpty()) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(207).body(failures);
    }
    
}
