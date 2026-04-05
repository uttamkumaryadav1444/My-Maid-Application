package MaidRepository.maid.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        // Create directory if not exists
        String directory = uploadDir + File.separator + subDirectory;
        Files.createDirectories(Paths.get(directory));

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = Paths.get(directory, fileName);

        // Save file
        Files.copy(file.getInputStream(), filePath);

        // Return relative URL
        return "/uploads/" + subDirectory + "/" + fileName;
    }

    public boolean deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        // Convert URL to file path
        String filePath = fileUrl.replace("/uploads/", uploadDir + File.separator);
        Path path = Paths.get(filePath);

        return Files.deleteIfExists(path);
    }

    public String getFileType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null) {
            return "unknown";
        }

        if (contentType.startsWith("image/")) {
            return "image";
        } else if (contentType.equals("application/pdf")) {
            return "pdf";
        } else if (contentType.equals("application/msword") ||
                contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            return "document";
        } else {
            return "other";
        }
    }
}