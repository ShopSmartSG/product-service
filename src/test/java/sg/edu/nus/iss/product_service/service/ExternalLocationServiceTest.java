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

    @Value("${profile.merchant.url}")
    private String merchantServiceUrl;

    @BeforeEach
    public void setUp() {
        externalLocationService = new ExternalLocationService(restTemplate);
    }

    @Test
    public void testGetCoordinatesByPincode_Success() {
        String pincode = "12345";
        LatLng expectedCoordinates = new LatLng(1.3521, 103.8198);
        String url = locationServiceUrl + "/coordinates?pincode=" + pincode;

        // Mock the restTemplate to return expected coordinates
        when(restTemplate.getForObject(url, LatLng.class)).thenReturn(expectedCoordinates);

        LatLng result = externalLocationService.getCoordinatesByPincode(pincode);

        assertNotNull(result);
        assertEquals(expectedCoordinates, result);
        verify(restTemplate).getForObject(url, LatLng.class);
    }

    @Test
    public void testGetCoordinatesByPincode_Failure() {
        String pincode = "12345";
        String url = locationServiceUrl + "/coordinates?pincode=" + pincode;

        // Mock the restTemplate to throw an exception
        when(restTemplate.getForObject(url, LatLng.class)).thenThrow(new RuntimeException("Service unavailable"));

        LatLng result = externalLocationService.getCoordinatesByPincode(pincode);

        assertNull(result);
        verify(restTemplate).getForObject(url, LatLng.class);
    }

    @Test
    public void testGetPincodeByMerchantId_Success() {
        UUID merchantId = UUID.randomUUID();
        MerchantDTO expectedMerchant = new MerchantDTO();
        expectedMerchant.setPincode("12345");
        String url = merchantServiceUrl + "/merchants/" + merchantId;

        // Mock the restTemplate to return the expected merchant
        when(restTemplate.getForObject(url, MerchantDTO.class)).thenReturn(expectedMerchant);

        String result = externalLocationService.getPincodeByMerchantId(merchantId);

        assertNotNull(result);
        assertEquals("12345", result);
        verify(restTemplate).getForObject(url, MerchantDTO.class);
    }

    @Test
    public void testGetPincodeByMerchantId_MerchantNotFound() {
        UUID merchantId = UUID.randomUUID();
        String url = merchantServiceUrl + "/merchants/" + merchantId;

        // Mock the restTemplate to return null (merchant not found)
        when(restTemplate.getForObject(url, MerchantDTO.class)).thenReturn(null);

        String result = externalLocationService.getPincodeByMerchantId(merchantId);

        assertNull(result);
        verify(restTemplate).getForObject(url, MerchantDTO.class);
    }

    @Test
    public void testGetPincodeByMerchantId_Failure() {
        UUID merchantId = UUID.randomUUID();
        String url = merchantServiceUrl + "/merchants/" + merchantId;

        // Mock the restTemplate to throw an exception
        when(restTemplate.getForObject(url, MerchantDTO.class)).thenThrow(new RuntimeException("Service unavailable"));

        String result = externalLocationService.getPincodeByMerchantId(merchantId);

        assertNull(result);
        verify(restTemplate).getForObject(url, MerchantDTO.class);
    }
}
