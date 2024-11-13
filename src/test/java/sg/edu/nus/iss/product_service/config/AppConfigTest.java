package sg.edu.nus.iss.product_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AppConfigTest {

    @Test
    void testRestTemplateBean() {
        // Create an application context with the AppConfig class
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        // Retrieve the RestTemplate bean from the context
        RestTemplate restTemplate = context.getBean(RestTemplate.class);

        // Verify that the RestTemplate bean is not null
        assertNotNull(restTemplate, "RestTemplate bean should not be null");
    }
}
