package sg.edu.nus.iss.product_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.iss.product_service.model.LatLng;
import sg.edu.nus.iss.product_service.dto.MerchantDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class ExternalLocationService {
    private final RestTemplate restTemplate;

    @Value("${location.service.url}")
    private String locationServiceUrl;

    @Value("${profile.merchant.url}")
    private String merchantServiceUrl;

    private static final Logger log = LoggerFactory.getLogger(ExternalLocationService.class);

    public ExternalLocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LatLng getCoordinatesByPincode(String pincode) {
        String url = locationServiceUrl + "/coordinates?pincode=" + pincode;
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

    public String getPincodeByMerchantId(UUID merchantId) {
        String url = merchantServiceUrl + "/merchants/" + merchantId;
        log.info("Calling merchant service to get pincode for merchant ID: {}", merchantId);
        try {
            MerchantDTO merchantDTO = restTemplate.getForObject(url, MerchantDTO.class);
            if (merchantDTO != null) {
                log.info("Received pincode '{}' for merchant ID: {}", merchantDTO.getPincode(), merchantId);
                return merchantDTO.getPincode();
            } else {
                log.warn("No merchant found with ID: {}", merchantId);
                return null;
            }
        } catch (Exception e) {
            log.error("Failed to fetch merchant data for ID {} from URL: {}. Error: {}", merchantId, url, e.getMessage(), e);
            return null;
        }
    }
}
