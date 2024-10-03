package sg.edu.nus.iss.product_service.service.strategy;

import sg.edu.nus.iss.product_service.model.Product;

import java.util.List;

public class PincodeFilterStrategy implements FilterStrategy {

    private final String pincode;

    public PincodeFilterStrategy(String pincode) {
        this.pincode = pincode;
    }

    @Override
    public List<Product> filter(List<Product> products) {
        return products.stream()
                .filter(product -> product.getPincode() != null && product.getPincode().equals(this.pincode)) // Make sure the field being compared exists
                .toList();
    }
}



