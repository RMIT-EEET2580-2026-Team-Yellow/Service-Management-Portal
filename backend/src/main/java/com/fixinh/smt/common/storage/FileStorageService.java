package com.fixinh.smt.common.storage;

import com.fixinh.smt.common.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Backs every "attach an image" feature (3.3.M1 cancel evidence, 3.3.M7 signed bill, 3.4.M2
 * rating evidence display) with one place that validates and persists an uploaded image.
 * Stores to local disk under {@code smt.storage.upload-dir} — sufficient for the dev-environment
 * deployment target (5.S1/5.M1); the directory is a mounted Docker volume in production so files
 * survive container restarts (see docker-compose.yml).
 */
@Service
public class FileStorageService {

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024 * 1024;

    private final Path uploadDir;

    public FileStorageService(@Value("${smt.storage.upload-dir:uploads}") String uploadDirPath) {
        this.uploadDir = Path.of(uploadDirPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory: " + this.uploadDir, e);
        }
    }

    /** Validates and persists an image, returning the URL clients use to retrieve it (served by
     *  StaticResourceConfig). Filenames are UUIDs, so the caller-supplied name is never trusted
     *  for the path — avoids collisions and directory-traversal from an attacker-controlled name. */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "file is required.");
        }
        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "File exceeds the 5MB limit.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "Only JPEG, PNG, or WEBP images are allowed (got \"" + contentType + "\").");
        }

        String filename = UUID.randomUUID() + extensionFor(contentType);

        try {
            Path target = uploadDir.resolve(filename).normalize();
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store the uploaded file.");
        }

        return "/api/v1/files/" + filename;
    }

    private String extensionFor(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
