package sg.edu.nus.iss.product_service.service.strategy;

import sg.edu.nus.iss.product_service.model.Product;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CategoryFilterStrategy implements FilterStrategy {

    private final UUID categoryId;

    public CategoryFilterStrategy(UUID categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public List<Product> filter(List<Product> products) {
        if (categoryId == null) {
            return products;
        }

        return products.stream()
                .filter(product -> product.getCategory().getCategoryId().equals(categoryId))
                .collect(Collectors.toList());
    }
}
