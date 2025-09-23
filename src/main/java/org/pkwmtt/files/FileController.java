package org.pkwmtt.files;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@RequestMapping("/admin/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService service;
    
    /**
     * @param file provided file
     * @return 200 if request ok
     * @throws IOException when file or directory malformed
     */
    @PostMapping(value = "/upload", consumes = MediaType.ALL_VALUE)
    public ResponseEntity<Void> upload (@RequestParam("file") MultipartFile file) throws IOException {
        service.upload(file);
        return ResponseEntity.ok().build();
    }
    
    /**
     * @param fileName name of requested file
     * @return file
     * @throws IOException problem with accessing selected file
     */
    @GetMapping(value = "/download/{fileName}")
    public ResponseEntity<UrlResource> download (@PathVariable String fileName) throws IOException {
        try {
            UrlResource resource = service.getResourceByFileName(fileName);
            return ResponseEntity
              .ok()
              .contentType(service.getContentNameByFileName(fileName))
              .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
              .body(resource);
        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
