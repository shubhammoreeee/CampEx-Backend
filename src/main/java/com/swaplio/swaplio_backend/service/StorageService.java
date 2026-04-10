package com.swaplio.swaplio_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${supabase.storage.bucket}")
    private String bucketName;

    @Value("${supabase.project.url}")
    private String projectUrl;

    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    /**
     * Uploads a file to Supabase Storage using the native REST API.
     * This bypasses the AWS S3 SDK entirely, avoiding signature issues.
     *
     * API: POST /storage/v1/object/{bucket}/{path}
     */
    public String uploadFile(MultipartFile file) {
        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // Build the upload URL
            String uploadUrl = projectUrl + "/storage/v1/object/" + bucketName + "/" + fileName;

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + serviceRoleKey);
            headers.set("apikey", serviceRoleKey);
            headers.setContentType(MediaType.parseMediaType(
                    file.getContentType() != null ? file.getContentType() : "application/octet-stream"
            ));

            // Create request with file bytes as body
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            // Make the upload request
            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                // Return the public URL
                return projectUrl + "/storage/v1/object/public/" + bucketName + "/" + fileName;
            } else {
                throw new RuntimeException("Upload failed with status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage(), e);
        }
    }
}