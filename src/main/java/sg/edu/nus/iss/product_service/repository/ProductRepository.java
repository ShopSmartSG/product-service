package sg.edu.nus.iss.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.edu.nus.iss.product_service.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Find products by category ID
    List<Product> findByCategoryCategoryId(UUID categoryId);

    // Find products by merchant ID
    List<Product> findByMerchantId(UUID merchantId);

    // Find products by pincode (assuming pincode is stored in product or related entities)
    List<Product> findByPincode(String pincode);

    // Custom query to filter products by price range
    List<Product> findByListingPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find all products by filters: category, merchant, and price range
    List<Product> findByCategoryCategoryIdAndMerchantIdAndListingPriceBetween(UUID categoryId, UUID merchantId, BigDecimal minPrice, BigDecimal maxPrice);

    // You can add more queries as per the need
}
