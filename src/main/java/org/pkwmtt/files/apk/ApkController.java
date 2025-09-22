package org.pkwmtt.files.apk;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.io.IOException;

@RequestMapping("${apiPrefix}/apk")
@RestController
@RequiredArgsConstructor
public class ApkController {
    
    private final ApkService apkService;
    
    @GetMapping("/download")
    public ResponseEntity<UrlResource> download () {
        try {
            return ResponseEntity
              .ok()
              .contentType(MediaType.parseMediaType("application/vnd.android.package-archive"))
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=PKWM_App.apk")
              .body(apkService.getApkResource());
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @GetMapping("/version")
    public String getApkVersion () {
        return "3.0.0";
    }
}
