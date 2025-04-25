package com.backend.roomie.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service for handling file uploads
 * This is a simplified implementation for demonstration purposes
 * In a production environment, you would typically use a cloud storage service
 */
@Service
public class FileUploadService {

    // Base directory for file uploads
    private final String uploadDir = "uploads";
    
    // Subdirectories for different types of uploads
    private final String profilePicturesDir = "profile-pictures";
    private final String propertyImagesDir = "property-images";
    
    /**
     * Upload a profile picture
     * @param file the file to upload
     * @return the URL of the uploaded file
     * @throws IOException if the file cannot be saved
     */
    public String uploadProfilePicture(MultipartFile file) throws IOException {
        // Create the directory if it doesn't exist
        createDirectoryIfNotExists(uploadDir + File.separator + profilePicturesDir);
        
        // Generate a unique filename
        String filename = generateUniqueFilename(file.getOriginalFilename());
        
        // Save the file
        Path filePath = Paths.get(uploadDir, profilePicturesDir, filename);
        Files.write(filePath, file.getBytes());
        
        // Return the URL (in a real application, this would be a full URL)
        return "/api/files/profile-pictures/" + filename;
    }
    
    /**
     * Upload a property image
     * @param file the file to upload
     * @return the URL of the uploaded file
     * @throws IOException if the file cannot be saved
     */
    public String uploadPropertyImage(MultipartFile file) throws IOException {
        // Create the directory if it doesn't exist
        createDirectoryIfNotExists(uploadDir + File.separator + propertyImagesDir);
        
        // Generate a unique filename
        String filename = generateUniqueFilename(file.getOriginalFilename());
        
        // Save the file
        Path filePath = Paths.get(uploadDir, propertyImagesDir, filename);
        Files.write(filePath, file.getBytes());
        
        // Return the URL (in a real application, this would be a full URL)
        return "/api/files/property-images/" + filename;
    }
    
    /**
     * Create a directory if it doesn't exist
     * @param dirPath the path of the directory to create
     */
    private void createDirectoryIfNotExists(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    /**
     * Generate a unique filename to avoid collisions
     * @param originalFilename the original filename
     * @return a unique filename
     */
    private String generateUniqueFilename(String originalFilename) {
        // Get the file extension
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        // Generate a random UUID and append the extension
        return UUID.randomUUID().toString() + extension;
    }
}