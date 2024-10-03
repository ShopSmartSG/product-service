package sg.edu.nus.iss.product_service.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.edu.nus.iss.product_service.model.Product;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByDeletedFalse();
    Page<Product> findByDeletedFalse(Pageable pageable);


    List<Product> findByMerchantIdAndDeletedFalse(UUID merchantId);

    List<Product> findByCategory_CategoryIdAndDeletedFalse(UUID categoryId);

    Page<Product> findByMerchantIdAndDeletedFalse(UUID merchantId, Pageable pageable);
    Page<Product> findByCategory_CategoryIdAndDeletedFalse(UUID categoryId, Pageable pageable);


    List<Product> findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(UUID merchantId, UUID categoryId);

    Page<Product> findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(UUID merchantId, UUID categoryId, Pageable pageable);

    Product findByProductIdAndDeletedFalse(UUID productId);
}