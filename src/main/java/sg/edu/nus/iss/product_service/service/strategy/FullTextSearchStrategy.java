package sg.edu.nus.iss.product_service.service.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

public class FullTextSearchStrategy implements FilterStrategy {

    private final String searchText;
    private final ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(FullTextSearchStrategy.class);

    public FullTextSearchStrategy(String searchText, ProductRepository productRepository) {
        this.searchText = searchText;
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> filter(List<Product> products) {
        log.info("Executing similarity search for: {}", searchText);

        double threshold = 0.1; // Adjust as needed
        List<Product> similarProducts = productRepository.findSimilarProducts(searchText, threshold);

        similarProducts.forEach(product ->
                log.info("Found Product: {}, Similarity Score: {}", product.getProductName(), threshold));

        return similarProducts;
    }
}