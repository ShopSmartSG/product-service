package sg.edu.nus.iss.product_service.service.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.iss.product_service.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class PriceFilterStrategy implements FilterStrategy {

    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private static final Logger log = LoggerFactory.getLogger(PriceFilterStrategy.class);
    public PriceFilterStrategy(BigDecimal minPrice, BigDecimal maxPrice) {
        this.minPrice = minPrice != null ? minPrice : BigDecimal.ZERO;
        this.maxPrice = maxPrice;
        log.info("Initialized PriceFilterStrategy with minPrice: {}, maxPrice: {}", minPrice, maxPrice);
    }

    @Override
    public List<Product> filter(List<Product> products) {
        log.info("Starting price filter with minPrice: {}, maxPrice: {}", minPrice, maxPrice);
        List<Product> filteredProducts = products.stream()
                .filter(product -> {
                    BigDecimal price = product.getListingPrice();
                    boolean matches = (minPrice == null || price.compareTo(minPrice) >= 0) &&
                            (maxPrice == null || price.compareTo(maxPrice) <= 0);

                    log.debug("Product '{}' with price '{}' matches: {}", product.getProductName(), price, matches);
                    return matches;
                })
                .collect(Collectors.toList());

        log.info("Price filter completed. Found {} matching products.", filteredProducts.size());
        return filteredProducts;
    }
}
