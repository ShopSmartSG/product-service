package sg.edu.nus.iss.product_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import sg.edu.nus.iss.product_service.dto.ProductFilterDTO;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.ProductRepository;
import sg.edu.nus.iss.product_service.service.strategy.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.CategoryRepository;
import sg.edu.nus.iss.product_service.repository.ProductRepository;

import java.util.ArrayList;
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

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository, ObjectMapper mapper) {
        this.mapper = mapper;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    public Page<Product> getAllProducts (UUID merchantId, Pageable pageable){
        return productRepository.findByMerchantIdAndDeletedFalse(merchantId, pageable);
    }

    public List<Product> getProductsByMerchantId (UUID merchantId){
        return productRepository.findByMerchantIdAndDeletedFalse(merchantId);
    }

    public Page<Product> getProductsByMerchantIdAndCategoryId (UUID merchantId, UUID categoryId, Pageable pageable){
        return productRepository.findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(merchantId, categoryId, pageable);
    }

    public List<Product> getProductsByMerchantIdAndCategoryId (UUID merchantId, UUID categoryId){
        return productRepository.findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(merchantId, categoryId);
    }

    public Product getProductByIdAndMerchantId (UUID merchantID, UUID productId){
        return productRepository.findByMerchantIdAndProductIdAndDeletedFalse(merchantID, productId);
    }

    public List<Product> getFilteredProducts(ProductFilterDTO filterDTO) {
        List<Product> allProducts = productRepository.findAll();
        List<FilterStrategy> strategies = new ArrayList<>();

        if (filterDTO.getPincode() != null) {
            strategies.add(new PincodeFilterStrategy(filterDTO.getPincode()));
        }
        if (filterDTO.getCategoryId() != null) {
            strategies.add(new CategoryFilterStrategy(filterDTO.getCategoryId()));
        }
        if (filterDTO.getMinPrice() != null || filterDTO.getMaxPrice() != null) {
            strategies.add(new PriceFilterStrategy(filterDTO.getMinPrice(), filterDTO.getMaxPrice()));
        }

        // Apply each strategy
        for (FilterStrategy strategy : strategies) {
            allProducts = strategy.filter(allProducts);
        }

        return allProducts;
    }
}