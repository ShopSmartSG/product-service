package sg.edu.nus.iss.product_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import sg.edu.nus.iss.product_service.dto.MerchantDTO;
import sg.edu.nus.iss.product_service.model.LatLng;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExternalLocationServiceTest {

    @InjectMocks
    private ExternalLocationService externalLocationService;

    @Mock
    private RestTemplate restTemplate;

    @Value("${location.service.url}")
    private String locationServiceUrl;

    @BeforeEach
    public void setUp() {
        externalLocationService = new ExternalLocationService(restTemplate);
    }

    @Test
    public void testGetCoordinatesByPincode_Failure() {
        String pincode = "12345";
        String url = locationServiceUrl + "/location/coordinates?pincode=" + pincode;

        // Mock the restTemplate to throw an exception
        when(restTemplate.getForObject(url, LatLng.class)).thenThrow(new RuntimeException("Service unavailable"));

        LatLng result = externalLocationService.getCoordinatesByPincode(pincode);

        assertNull(result);
        verify(restTemplate).getForObject(url, LatLng.class);
    }
}
