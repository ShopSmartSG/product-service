package sg.edu.nus.iss.product_service.service.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.service.CategoryService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CategoryFilterStrategy implements FilterStrategy {

    private final UUID categoryId;
    public CategoryFilterStrategy(UUID categoryId) {
        this.categoryId = categoryId;
    }
    private static final Logger log = LoggerFactory.getLogger(CategoryFilterStrategy.class);

    @Override
    public List<Product> filter(List<Product> products) {
        log.info("Starting category filter. Category ID: {}", categoryId);
        if (categoryId == null) {
            log.warn("Category ID is null. Returning all products without filtering.");
            return products;
        }

        List<Product> filteredProducts = products.stream()
                .filter(product -> {
                    UUID productCategoryId = product.getCategory().getCategoryId();
                    boolean matches = productCategoryId.equals(categoryId);
                    log.debug("Product: {}, Category ID: {} -> Match: {}",
                            product.getProductName(), productCategoryId, matches);
                    return matches;
                })
                .collect(Collectors.toList());

        log.info("Category filter completed. Found {} matching products.", filteredProducts.size());
        return filteredProducts;
    }
}
