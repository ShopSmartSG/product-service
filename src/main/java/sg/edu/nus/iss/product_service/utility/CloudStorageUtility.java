package sg.edu.nus.iss.product_service.utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
public class CloudStorageUtility {
    private final String bucketName;
    private final String projectId;
    private final String credentialsPath;
    private final Storage storage;

    Logger logger = LoggerFactory.getLogger(CloudStorageUtility.class);

    public CloudStorageUtility(
            @Value("${gcp.cloud.storage.bucket-name}") String bucketName,
            @Value("${gcp.project.id}") String projectId,
            @Value("${spring.cloud.gcp.credentials.location}") String credentialsPath) {
        this.bucketName = bucketName;
        this.projectId = projectId;
        this.credentialsPath = credentialsPath;
        
        try {
            String actualPath = credentialsPath;

            if (actualPath.startsWith("file:")) {
                actualPath = actualPath.substring(5);
            }
            
            GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(actualPath));
            
            this.storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();

            logger.info("Storage initialized successfully with path: {}", credentialsPath);
                
        } catch (IOException e) {
            logger.error("Failed to initialize storage with path: {}", credentialsPath, e);
            throw new RuntimeException("Failed to initialize storage: " + e.getMessage(), e);
        }
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileBaseName = FilenameUtils.getBaseName(file.getOriginalFilename());
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "_" + fileBaseName + "_" + 
                         LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")) + 
                         "." + fileExtension;

        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(getContentType(file))
                .build();

        storage.create(blobInfo, file.getBytes());
        logger.info("File uploaded successfully: {}", fileName);
        
        return fileName;
    }

    private String getContentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }

    public String getFileUrl(String fileName) {
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }
}