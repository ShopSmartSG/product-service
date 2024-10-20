package sg.edu.nus.iss.product_service.service.strategy;

import sg.edu.nus.iss.product_service.model.Product;
import java.util.UUID;

public interface ProductStrategy {
    Product addProduct(Product product);
    Product updateProduct(UUID productId, Product product);
    void deleteProduct(UUID productId);
}