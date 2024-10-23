package sg.edu.nus.iss.product_service.service.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.iss.product_service.model.Product;

import java.util.List;

public class PincodeFilterStrategy implements FilterStrategy {

    private final String pincode;
    private static final Logger log = LoggerFactory.getLogger(PincodeFilterStrategy.class);
    public PincodeFilterStrategy(String pincode) {
        this.pincode = pincode;
        log.info("Initialized PincodeFilterStrategy with pincode: {}", pincode);
    }

    @Override
    public List<Product> filter(List<Product> products) {
        log.info("Starting pincode filter for pincode: {}", pincode);
        List<Product> filteredProducts = products.stream()
                .filter(product -> {
                    String productPincode = product.getPincode();
                    if (productPincode == null) {
                        log.warn("Product '{}' has no pincode. Skipping.", product.getProductName());
                        return false;
                    }

                    boolean matches = productPincode.equals(this.pincode);
                    log.debug("Product '{}' with pincode '{}' matches: {}", product.getProductName(), productPincode, matches);
                    return matches;
                })
                .toList();

        log.info("Pincode filter completed. Found {} matching products.", filteredProducts.size());
        return filteredProducts;
    }
}



