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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ApkService {
    @Value("${app.upload.dir:uploads}")
    private String FILES_DIR;
    
    private final FileService fileService;
    
    public UrlResource getApkResource () throws IOException, IllegalArgumentException {
        Path filePath = findNewestApkByExtensionInUploads().orElseThrow(FileNotFoundException::new);
        return fileService.getResourceByFileName(filePath.getFileName().toString());
    }
    
    public String getApkVersion () throws IOException {
        Path filePath = findNewestApkByExtensionInUploads().orElseThrow(IOException::new);
        String fileName = filePath.getFileName().toString();
        Pattern pattern = Pattern.compile("\\d+(?:\\.\\d+){1,2}");
        Matcher matcher = pattern.matcher(fileName);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group();
    }
    
    private Optional<Path> findNewestApkByExtensionInUploads () throws IOException, IllegalArgumentException {
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
                      throw new RuntimeException("Couldn't locate last modified file");
                  }
              }));
        }
    }
}
