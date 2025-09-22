package org.pkwmtt.files;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${app.upload.dir:uploads}")
    private String UPLOADS_DIR;
    
    public void upload (MultipartFile file) throws IOException {
        Path projectRoot = Paths.get("").toAbsolutePath();
        Path uploadPath = projectRoot.resolve(UPLOADS_DIR);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(Objects.requireNonNull(file.getOriginalFilename()));
        file.transferTo(filePath.toFile());
    }
    
    public UrlResource getResourceByFileName (String fileName) throws IOException {
        //Dir: ProjectRoot/uploads/fileName
        Path filePath = getFilePathByName(fileName);
        
        UrlResource resource = new UrlResource(filePath.toUri());
        
        if (!resource.exists()) {
            throw new FileNotFoundException();
        }
        
        return resource;
        
    }
    
    public MediaType getContentNameByFileName (String fileName) throws IOException {
        Path filePath = getFilePathByName(fileName);
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return MediaType.parseMediaType(contentType);
    }
    
    private Path getFilePathByName (String fileName) {
        return Paths.get("").toAbsolutePath().resolve(UPLOADS_DIR).resolve(fileName).normalize();
    }
}
