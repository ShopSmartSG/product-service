package sg.edu.nus.iss.product_service.service.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.model.LatLng;
import sg.edu.nus.iss.product_service.service.ExternalLocationService;

import java.util.List;
import java.util.stream.Collectors;

public class LocationFilterStrategy implements FilterStrategy {
    private final LatLng targetCoordinates;
    private final double rangeInKm;
    private final ExternalLocationService locationService;
    private static final Logger log = LoggerFactory.getLogger(LocationFilterStrategy.class);

    public LocationFilterStrategy(LatLng targetCoordinates, double rangeInKm, ExternalLocationService locationService) {
        this.targetCoordinates = targetCoordinates;
        this.rangeInKm = rangeInKm;
        this.locationService = locationService;
    }

    @Override
    public List<Product> filter(List<Product> products) {
        log.info("Starting location filter. Target Coordinates: {}, Range: {} km", targetCoordinates, rangeInKm);
        return products.stream()
            .filter(product -> {
                // Use merchant's merchantId to get pincode
                String pincode = product.getPincode();
                if (pincode == null) {
                    log.warn("No pincode found: {}", product.getPincode());
                    return false;
                }

                // Now get coordinates from the pincode
                LatLng merchantCoordinates = locationService.getCoordinatesByPincode(pincode);
                if (merchantCoordinates == null) {
                    log.warn("Coordinates not found for pincode: {}", pincode);
                    return false;
                }

                // Calculate distance between target and merchant coordinates
                double distance = calculateDistance(
                        targetCoordinates.getLat(),
                        targetCoordinates.getLng(),
                        merchantCoordinates.getLat(),
                        merchantCoordinates.getLng()
                );
                log.info("Product ID: {} - Distance: {} km", product.getProductId(), distance);
                System.out.printf("Product ID: %s - Distance: %.2f km%n", product.getProductId(), distance);
                return distance <= rangeInKm;
            })
            .collect(Collectors.toList());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371; // Radius in kilometers
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS * c;
        log.debug("Calculated Haversine distance: {} km", distance);
        return distance;
    }
}
