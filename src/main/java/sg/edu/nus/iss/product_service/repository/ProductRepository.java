package sg.edu.nus.iss.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.edu.nus.iss.product_service.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByDeletedFalse();
    Page<Product> findByDeletedFalse(Pageable pageable);

    // Find products by category ID
    List<Product> findByCategoryCategoryId(UUID categoryId);

    // Find products by merchant ID
    List<Product> findByMerchantId(UUID merchantId);
    List<Product> findByMerchantIdAndDeletedFalse(UUID merchantId);

    // Find products by pincode (assuming pincode is stored in product or related entities)
    List<Product> findByPincode(String pincode);
    List<Product> findByCategory_CategoryIdAndDeletedFalse(UUID categoryId);

    Page<Product> findByMerchantIdAndDeletedFalse(UUID merchantId, Pageable pageable);
    Page<Product> findByCategory_CategoryIdAndDeletedFalse(UUID categoryId, Pageable pageable);

    // Custom query to filter products by price range
    List<Product> findByListingPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // Find all products by filters: category, merchant, and price range
    List<Product> findByCategoryCategoryIdAndMerchantIdAndListingPriceBetween(UUID categoryId, UUID merchantId, BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(UUID merchantId, UUID categoryId);

    Page<Product> findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(UUID merchantId, UUID categoryId, Pageable pageable);

    // You can add more queries as per the need
    Product findByProductIdAndDeletedFalse(UUID productId);

    Product findByMerchantIdAndProductIdAndDeletedFalse(UUID merchantID, UUID productId);
}
