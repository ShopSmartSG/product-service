package sg.edu.nus.iss.product_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.message.Message;
import org.bson.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import sg.edu.nus.iss.product_service.dto.ProductFilterDTO;
import sg.edu.nus.iss.product_service.model.LatLng;
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
import java.math.BigDecimal;
import java.util.*;
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
    private final ExternalLocationService locationService;
    private final ObjectMapper mapper;

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository, ObjectMapper mapper, ExternalLocationService locationService) {
        this.mapper = mapper;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.locationService = locationService;
    }
    public Page<Product> getAllProducts (UUID merchantId, Pageable pageable){
        log.info("Fetching all products for merchantId: {}", merchantId);
        return productRepository.findByMerchantIdAndDeletedFalse(merchantId, pageable);
    }

    public List<Product> getProductsByMerchantId (UUID merchantId){
        log.info("Fetching products for merchantId: {}", merchantId);
        return productRepository.findByMerchantIdAndDeletedFalse(merchantId);
    }

    public Page<Product> getProductsByMerchantIdAndCategoryId (UUID merchantId, UUID categoryId, Pageable pageable){
        log.info("Fetching products for merchantId: {} and categoryId: {}", merchantId, categoryId);
        return productRepository.findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(merchantId, categoryId, pageable);
    }

    public List<Product> getProductsByMerchantIdAndCategoryId (UUID merchantId, UUID categoryId){
        log.info("Fetching products for merchantId : {} and categoryId : {}", merchantId, categoryId);
        return productRepository.findByMerchantIdAndCategory_CategoryIdAndDeletedFalse(merchantId, categoryId);
    }

    public Product getProductByIdAndMerchantId (UUID merchantID, UUID productId){
        log.info("Fetching product with ID: {} for merchantId: {}", productId, merchantID);
        return productRepository.findByMerchantIdAndProductIdAndDeletedFalse(merchantID, productId);
    }

//    public List<Product> getProductsByIds(List<String> productIds) {
//        log.info("Fetching products for product IDs: {}", productIds);
//
//        // Convert the list of strings into a list of UUIDs
//        List<UUID> ids = productIds.stream()
//                .map(UUID::fromString)
//                .toList();
//
//        // Fetch products matching the given IDs
//        return productRepository.findByProductIdInAndDeletedFalse(ids);
//    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getProductsByIds(List<String> productIds) {
        log.info("Fetching products for product IDs: {}", productIds);

        // Convert string IDs to UUIDs
        List<UUID> ids = productIds.stream()
                .map(UUID::fromString)
                .toList();



        // Fetch products from the repository
        List<Product> products = productRepository.findByProductIdInAndDeletedFalse(ids);

        // Check if any products were found
        if (products.isEmpty()) {
            ObjectNode response = mapper.createObjectNode();
            response.put("message","No products with matching ids available");
            log.warn("No products found for the provided IDs: {}", productIds);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);  // Return 404 status
        } else {
            log.info("Found {} products for the provided IDs.", products.size());
            return ResponseEntity.ok(products);  // Return 200 OK with the product list
        }
    }

    public List<Product> getFilteredProducts(ProductFilterDTO filterDTO) {
        List<Product> allProducts = productRepository.findByDeletedFalse();
        List<FilterStrategy> strategies = new ArrayList<>();

        try{
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
            if (filterDTO.getPincode() != null) {
                LatLng targetCoordinates = locationService.getCoordinatesByPincode(filterDTO.getPincode());
                double range = (filterDTO.getRangeInKm() != null) ? filterDTO.getRangeInKm() : 5.0;  // Default to 5 km
                if (targetCoordinates != null) {
                    strategies.add(new LocationFilterStrategy(targetCoordinates, range, locationService));
                }
            }

            // If no strategies are added, return all products
            if (strategies.isEmpty()) {
                log.warn("No filter criteria applied. Returning all products.");
                return allProducts;  // Return all products if no filters are applied
            }

            // Apply each strategy
            for (FilterStrategy strategy : strategies) {
                log.info("Filtered products count: {}", allProducts.size());
                allProducts = strategy.filter(allProducts);
            }

            return allProducts;
        }catch (Exception e) {
            // Log the exception
            log.error("Error occurred while filtering products: {}", e.getMessage(), e);
            // Return an empty list or handle it according to your application's logic
            return Collections.emptyList();
        }
    }
}