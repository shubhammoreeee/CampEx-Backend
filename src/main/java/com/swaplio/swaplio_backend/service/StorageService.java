package com.swaplio.swaplio_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
            String originalName = file.getOriginalFilename();

// 🔥 REMOVE SPACES + SPECIAL CHARS
            String cleanName = originalName
                    .replaceAll("\\s+", "_")   // spaces → _
                    .replaceAll("[^a-zA-Z0-9._-]", ""); // remove special chars
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

    public void deleteFile(String fileUrl) {

        try {
            if (fileUrl == null || !fileUrl.contains("/object/public/")) {
//                System.out.println("Invalid Supabase URL: " + fileUrl);
                return;
            }

            // Extract file path safely
            String path = fileUrl.split("/object/public/")[1];

            // Build correct delete URL
            String deleteUrl = projectUrl + "/storage/v1/object/" + path;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + serviceRoleKey);
            headers.set("apikey", serviceRoleKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

//            System.out.println("Deleting file: " + deleteUrl);

            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, String.class);

//            System.out.println("File deleted successfully");

        } catch (HttpClientErrorException e) {

            // ✅ MOST IMPORTANT FIX
            if (e.getStatusCode().value() == 404) {
                System.out.println("File already deleted (safe to ignore): " + fileUrl);
            } else {
                System.out.println("Delete failed: " + e.getResponseBodyAsString());
                throw e;
            }

        } catch (Exception e) {
            System.out.println("Unexpected error while deleting file: " + e.getMessage());
        }
    }
}