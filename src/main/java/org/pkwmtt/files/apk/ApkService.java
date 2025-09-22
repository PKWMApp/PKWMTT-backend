package org.pkwmtt.files.apk;

import lombok.RequiredArgsConstructor;
import org.pkwmtt.files.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ApkService {
    @Value("${app.upload.dir:uploads}")
    private String FILES_DIR;
    
    private final FileService fileService;
    
    public UrlResource getApkResource () throws IOException {
        Path filePath = findApkByExtensionInUploads().orElseThrow(FileNotFoundException::new);
        return fileService.getResourceByFileName(filePath.getFileName().toString());
    }
    
    private Optional<Path> findApkByExtensionInUploads () throws IOException {
        Path dirPath = Paths.get(FILES_DIR);
        
        if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
            throw new IllegalArgumentException("Invalid directory: " + dirPath);
        }
        
        Stream<Path> stream = Files.list(dirPath);
        
        try (stream) {
            return stream
              .filter(Files::isRegularFile)
              .filter(file -> file.getFileName().toString().toLowerCase().endsWith(".apk"))
              .max(Comparator.comparingLong(file -> {
                  try {
                      return Files.getLastModifiedTime(file).toMillis();
                  } catch (IOException e) {
                      throw new RuntimeException(e); //TODO handle
                  }
              }));
        }
    }
}
