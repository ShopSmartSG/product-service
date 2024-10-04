package sg.edu.nus.iss.product_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.CategoryRepository;
import sg.edu.nus.iss.product_service.repository.ProductRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper mapper;

    @Autowired
    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository, ObjectMapper mapper) {
        this.mapper = mapper;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }


    @Transactional
    public Product updateProduct(Product product) {
        product.setUpdatedBy("merchant");
        product.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Singapore")).toInstant()));
        return productRepository.save(product);
    }

    @Transactional
    public Product deleteProduct(Product product) {
        product.setUpdatedBy("merchant");
        product.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Singapore")).toInstant()));
        product.setDeleted(true);
        return productRepository.save(product);
    }

    @Transactional
    public Product addProduct(Product product) {
        product.setCreatedBy("merchant");
        product.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Singapore")).toInstant()));
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findByDeletedFalse();
    }

    public Page<Product> getAllProducts(UUID merchantId, Pageable pageable) {
        return productRepository.findByMerchantIdAndDeletedFalse(merchantId,pageable);
    }

    public List<Product> getProductsByMerchantId(UUID merchantId) {
        return productRepository.findByMerchantIdAndDeletedFalse(merchantId);
    }

    public Page<Product> getProductsByMerchantId(UUID merchantId, Pageable pageable) {
        return productRepository.findByMerchantIdAndDeletedFalse(merchantId, pageable);
    }

    public List<Product> getProductsByCategoryId(UUID categoryId) {
        return productRepository.findByCategory_CategoryIdAndDeletedFalse(categoryId);
    }

    public Page<Product> getProductsByCategoryId(UUID categoryId, Pageable pageable) {
        return productRepository.findByCategory_CategoryIdAndDeletedFalse(categoryId, pageable);
    }

    public Page<Product> getProductsByMerchantIdAndCategoryId(UUID merchantId, UUID categoryId, Pageable pageable) {
        return productRepository.findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(merchantId,categoryId, pageable);
    }

    public List<Product> getProductsByMerchantIdAndCategoryId(UUID merchantId, UUID categoryId) {
        return productRepository.findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(merchantId,categoryId);
    }

    public Product getProductByIdAndMerchantId(UUID merchantID,UUID productId) {
        return productRepository.findByMerchantIdAndProductIdAndDeletedFalse(merchantID,productId);
    }



}