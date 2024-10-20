package sg.edu.nus.iss.product_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import sg.edu.nus.iss.product_service.dto.ProductFilterDTO;
import sg.edu.nus.iss.product_service.model.LatLng;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.ProductRepository;
import sg.edu.nus.iss.product_service.service.strategy.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sg.edu.nus.iss.product_service.repository.CategoryRepository;

import java.math.BigDecimal;
import java.util.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ExternalLocationService locationService;
    private final ObjectMapper mapper;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository, ObjectMapper mapper, ExternalLocationService locationService) {
        this.mapper = mapper;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.locationService = locationService;
    }

    @Transactional
    public Product updateProduct(Product product) {
        product.setUpdatedBy("merchant");
        product.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Singapore")).toInstant()));
        return productRepository.save(product);
    }

    @Transactional
    public Product deleteProduct (Product product){
        product.setUpdatedBy("merchant");
        product.setUpdatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Singapore")).toInstant()));
        product.setDeleted(true);
        return productRepository.save(product);
    }

    @Transactional
    public Product addProduct (Product product){
        product.setCreatedBy("merchant");
        product.setCreatedAt(Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Singapore")).toInstant()));
        return productRepository.save(product);
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

        try{
            if (filterDTO.getPincode() != null && !filterDTO.getPincode().isEmpty()) {
                strategies.add(new PincodeFilterStrategy(filterDTO.getPincode()));
            }
            if (filterDTO.getCategoryId() != null && !filterDTO.getCategoryId().toString().isEmpty()) {
                strategies.add(new CategoryFilterStrategy(filterDTO.getCategoryId()));
            }
            if (filterDTO.getMinPrice() != null && filterDTO.getMinPrice().compareTo(BigDecimal.ZERO) > 0 ||
                    filterDTO.getMaxPrice() != null && filterDTO.getMaxPrice().compareTo(BigDecimal.ZERO) > 0) {
                strategies.add(new PriceFilterStrategy(filterDTO.getMinPrice(), filterDTO.getMaxPrice()));
            }
            // Add strategy for full-text search if searchText is provided
            if (filterDTO.getSearchText() != null && !filterDTO.getSearchText().isEmpty()) {
                strategies.add(new FullTextSearchStrategy(filterDTO.getSearchText(), productRepository));
            }
            if (filterDTO.getPincode() != null && filterDTO.getRangeInKm() != null) {
                LatLng targetCoordinates = locationService.getCoordinatesByPincode(filterDTO.getPincode());
                if (targetCoordinates != null) {
                    strategies.add(new LocationFilterStrategy(targetCoordinates, filterDTO.getRangeInKm(), locationService));
                }
            }

            // If no strategies are added, return all products
            if (strategies.isEmpty()) {
                return allProducts;  // Return all products if no filters are applied
            }

            // Apply each strategy
            for (FilterStrategy strategy : strategies) {
                allProducts = strategy.filter(allProducts);
            }

            return allProducts;
        }catch (Exception e) {
            // Log the exception
            System.err.println("Error occurred while filtering products: " + e.getMessage());
            // Return an empty list or handle it according to your application's logic
            return Collections.emptyList();
        }
    }
}