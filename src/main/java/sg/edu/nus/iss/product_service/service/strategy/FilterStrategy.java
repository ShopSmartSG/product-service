package sg.edu.nus.iss.product_service.service.strategy;

import sg.edu.nus.iss.product_service.model.Product;

import java.util.List;

public interface FilterStrategy {
    List<Product> filter(List<Product> products);
}

