package sg.edu.nus.iss.product_service.service.strategy;

import sg.edu.nus.iss.product_service.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class PriceFilterStrategy implements FilterStrategy {

    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;

    public PriceFilterStrategy(BigDecimal minPrice, BigDecimal maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @Override
    public List<Product> filter(List<Product> products) {
        return products.stream()
                .filter(product -> {
                    BigDecimal price = product.getListingPrice();
                    return (minPrice == null || price.compareTo(minPrice) >= 0) &&
                            (maxPrice == null || price.compareTo(maxPrice) <= 0);
                })
                .collect(Collectors.toList());
    }
}
