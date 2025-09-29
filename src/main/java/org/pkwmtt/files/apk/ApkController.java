package org.pkwmtt.files.apk;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequestMapping("${apiPrefix}/apk")
@RestController
@RequiredArgsConstructor
public class ApkController {
    
    private final ApkService apkService;
    
    @GetMapping("/download")
    public ResponseEntity<UrlResource> download (HttpServletRequest request) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.android.package-archive"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("PKWM_App.apk").build());
        
        
        String origin = request.getHeader("Origin");
        
        if (origin == null || origin.isBlank()) {
            return ResponseEntity.ok().headers(headers).body(apkService.getApkResource());
        }
        
        List<String> allowedOrigins = List.of("https://pkwmapp.pl", "http://localhost:3000");
        if (allowedOrigins.contains(origin)) {
            headers.set("Access-Control-Allow-Origin", origin);
        }
        
        
        return ResponseEntity.ok().headers(headers).body(apkService.getApkResource());
    }
    
    @GetMapping("/version")
    public String getApkVersion () throws IOException {
        return apkService.getApkVersion();
    }
}
