package sg.edu.nus.iss.product_service.service.strategy;

import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

public class FullTextSearchStrategy implements FilterStrategy {

    private final String searchText;
    private final ProductRepository productRepository;

    public FullTextSearchStrategy(String searchText, ProductRepository productRepository) {
        this.searchText = searchText;
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> filter(List<Product> products) {
        return products.stream()
                .filter(product -> {
                    double score = productRepository.similarity(product.getProductName(), searchText);
                    System.out.println("Searching for: " + searchText);
                    System.out.println("Product: " + product.getProductName() + ", Similarity Score: " + score);
                    return score > 0.5; // Adjust threshold as needed
                })
                .collect(Collectors.toList());
    }
}