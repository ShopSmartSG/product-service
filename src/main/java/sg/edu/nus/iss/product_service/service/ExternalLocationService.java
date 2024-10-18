package sg.edu.nus.iss.product_service.service;

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

    public ExternalLocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LatLng getCoordinatesByPincode(String pincode) {
        try {
            String url = locationServiceUrl + "/coordinates?pincode=" + pincode;
            return restTemplate.getForObject(url, LatLng.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getPincodeByMerchantId(UUID merchantId) {
        try {
            String url = merchantServiceUrl + "/merchants/" + merchantId;
            MerchantDTO merchantDTO = restTemplate.getForObject(url, MerchantDTO.class);
            return merchantDTO != null ? merchantDTO.getPincode() : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
