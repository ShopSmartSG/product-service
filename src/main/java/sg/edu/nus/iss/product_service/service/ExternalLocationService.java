package sg.edu.nus.iss.product_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.iss.product_service.model.LatLng;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalLocationService {
    private final RestTemplate restTemplate;

    @Value("${location.service.url}")
    private String locationServiceUrl;

    private static final Logger log = LoggerFactory.getLogger(ExternalLocationService.class);

    public ExternalLocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LatLng getCoordinatesByPincode(String pincode) {
        String url = locationServiceUrl + "/location/coordinates?pincode=" + pincode;
        log.info("Calling location service to get coordinates for pincode: {}", pincode);
        try {
            LatLng response = restTemplate.getForObject(url, LatLng.class);
            log.info("Received coordinates for pincode {}: {}", pincode, response);
            return restTemplate.getForObject(url, LatLng.class);
        } catch (Exception e) {
            log.error("Failed to fetch coordinates for pincode {} from URL: {}. Error: {}", pincode, url, e.getMessage(), e);
            return null;
        }
    }
}
