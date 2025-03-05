// File: src/main/java/sg/edu/nus/iss/product_service/utility/MockCloudStorageUtility.java
package sg.edu.nus.iss.product_service.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("zapscan")
@Primary
@Component
public class MockCloudStorageUtility extends CloudStorageUtility {

    Logger logger = LoggerFactory.getLogger(MockCloudStorageUtility.class);

    // Constructor that doesn't require any file access
    public MockCloudStorageUtility() {
        // Call super with null or dummy values
        // The exact parameters depend on your CloudStorageUtility constructor
        super(null, null,null);

        // If needed, initialize any fields that might be used during the scan
    }

    // Override any methods that might be called during the scan
    // For example:
    public void uploadFile(String filePath, String destination) {
        // Do nothing or minimal implementation
        logger.info("Mock upload: {} to {}", filePath, destination);
    }


    public byte[] downloadFile(String filePath) {
        // Return empty or dummy data
        logger.info("Mock download: {}", filePath);
        return new byte[0];
    }

    // Override other methods as needed
}