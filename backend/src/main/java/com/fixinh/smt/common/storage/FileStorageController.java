package com.fixinh.smt.common.storage;

import com.fixinh.smt.common.web.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Shared upload endpoint used by every image-evidence feature. Any authenticated Staff (Admin,
 * Receptionist, or Technician) may upload — the specific rule about who may attach evidence to
 * what is enforced by the booking/technician endpoint that receives the resulting URL, not here.
 * Retrieval (GET /api/v1/files/**) is served separately by StaticResourceConfig and is public,
 * since URLs are unguessable UUIDs and an {@code <img>} tag cannot send an Authorization header.
 */
@RestController
@RequestMapping("/api/v1/files")
public class FileStorageController {

    private final FileStorageService fileStorageService;

    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping
    public ApiResponse<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(Map.of("url", fileStorageService.store(file)));
    }
}
