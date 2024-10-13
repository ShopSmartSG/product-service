package sg.edu.nus.iss.product_service.utility;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class S3UtilityTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Utility s3Utility;

    // Remove @Value here since it's not processed in tests
    private String bucketName;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bucketName = "test-bucket"; // Set the mock bucket name
        s3Utility = new S3Utility(s3Client); // Initialize with the mocked S3 client
        // Manually set the bucketName to the S3Utility instance
        ReflectionTestUtils.setField(s3Utility, "bucketName", bucketName);
    }

    @Test
    public void testUploadFile() throws IOException {
        // Arrange
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test.txt");
        when(mockFile.getBytes()).thenReturn("Sample content".getBytes());

        // Act
        String result = s3Utility.uploadFile(mockFile);

        // Assert
        assertEquals(String.format("https://%s.s3.amazonaws.com/%s", bucketName, result), s3Utility.getFileUrl(result));
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    public void testGetFileUrl() {
        // Arrange
        String fileName = "file123";

        // Act
        String fileUrl = s3Utility.getFileUrl(fileName);

        // Assert
        assertEquals(String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName), fileUrl);
    }
}
