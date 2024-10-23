package sg.edu.nus.iss.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sg.edu.nus.iss.product_service.dto.ProductDTO;
import sg.edu.nus.iss.product_service.exception.ResourceNotFoundException;
import sg.edu.nus.iss.product_service.model.Category;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.service.CategoryService;
import sg.edu.nus.iss.product_service.service.ProductService;
import sg.edu.nus.iss.product_service.utility.S3Utility;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/merchants")
@Tag(name = "Merchant Product API", description = "APIs for merchants to create, read, update, and delete products")
public class MerchantProductController {
    private final ProductService productService;
    private final S3Utility s3Service;
    private final ObjectMapper objectMapper;
    private final CategoryService categoryService;

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    public MerchantProductController(ProductService productService, ObjectMapper objectMapper, S3Utility s3Service, CategoryService categoryService) {
        this.productService = productService;
        this.objectMapper = objectMapper;
        this.s3Service = s3Service;
        this.categoryService = categoryService;
    }

    @GetMapping("/{merchantId}/products")
    @Operation(summary = "Retrieve all products")
    public ResponseEntity<?> getAllProducts(@PathVariable UUID merchantId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        log.info("Fetching all products for merchantId: {}", merchantId);
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getAllProducts(merchantId, pageable);
            log.info("Found {} products for merchant with pagination", products.getTotalElements());
            return ResponseEntity.ok(products);
        } else {
            List<Product> products = productService.getProductsByMerchantId(merchantId);
            log.info("Found {} products for merchant", products.size());
            return ResponseEntity.ok(products);
        }
    }

    @GetMapping("/{merchantId}/products/{productId}")
    @Operation(summary = "Retrieve product By ID")
    public ResponseEntity<?> getProductById(@PathVariable UUID merchantId, @PathVariable UUID productId) {
        log.info("Fetching product with ID: {} for merchantId: {}", productId, merchantId);
        Product product = productService.getProductByIdAndMerchantId(merchantId, productId);
        if (product == null) {
            log.warn("Product not found for ID: {}", productId);
            throw new ResourceNotFoundException("Product not found");
        }
        log.info("Product retrieved successfully: {}", product);
        return ResponseEntity.ok(product);
    }


    @GetMapping("/{merchantId}/categories/{categoryId}")
    @Operation(summary = "Retrieve products by merchant ID and category ID")
    public ResponseEntity<?> getProductByMerchantIdAndCategoryId(@PathVariable UUID merchantId, @PathVariable UUID categoryId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        log.info("Fetching products for merchantId: {} in categoryId: {}", merchantId, categoryId);
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId, pageable);
            log.info("Found {} products with pagination", products.getTotalElements());
            return ResponseEntity.ok(products);
        } else {
            List<Product> products = productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId);
            log.info("Found {} products", products.size());
            return ResponseEntity.ok(products);
        }
    }

    @PostMapping("/products")
    @Operation(summary = "Add a new product")
    public ResponseEntity<?> addProduct( @Valid @RequestBody Product product) {
        // check if category exists
        // if it doesn't ask merchant to create category first
        Category category = categoryService.getCategoryByName(product.getCategory().getCategoryName());
        log.info("Adding new product: {}", product);
        if (category == null) {
            log.warn("Category not found: {}", product.getCategory().getCategoryName());
            throw new ResourceNotFoundException("Category not found , Please create category first");
        }
        product.setCategory(category);
        Product newProduct = productService.addProduct(product);
        log.info("Product added successfully: {}", newProduct);
        return ResponseEntity.ok(newProduct);
    }

    @PostMapping("/images/upload")
    @Operation(summary = "Upload a product image", description = "Uploads a product image to S3")
    public ResponseEntity<String> uploadImage(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "File to upload",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestPart("file") MultipartFile file
    ) throws IOException {
        log.info("Uploading product image: {}", file.getOriginalFilename());
        String fileName = s3Service.uploadFile(file);
        String fileUrl = s3Service.getFileUrl(fileName);
        log.info("Image uploaded successfully: {}", fileUrl);
        return ResponseEntity.ok(fileUrl);
    }

    @DeleteMapping("/{merchantId}/products/{productId}")
    @Operation(summary = "Delete product by Product ID")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID merchantId, @PathVariable UUID productId) {
        log.info("Deleting product with ID: {} for merchantId: {}", productId, merchantId);
        Product existingProduct = productService.getProductByIdAndMerchantId(merchantId, productId);
        if (existingProduct == null) {
            log.warn("Product not found for deletion: {}", productId);
            throw new ResourceNotFoundException("Product not found");
        }
        productService.deleteProduct(existingProduct);
        log.info("Product deleted successfully: {}", productId);
        return ResponseEntity.ok("Delete: successful");
    }

    @PutMapping("/{merchantId}/products/{productId}")
    @Operation(summary = "Update product")
    public ResponseEntity<?> updateProduct(@PathVariable UUID merchantId, @PathVariable UUID productId, @Valid @RequestBody ProductDTO dto) {
        log.info("Updating product with ID: {} for merchantId: {}", productId, merchantId);
        Product existingProduct = productService.getProductByIdAndMerchantId(merchantId, productId);
        if (existingProduct == null) {
            log.warn("Product not found for update: {}", productId);
            throw new ResourceNotFoundException("Product not found");
        }

        if (!existingProduct.getProductId().equals(dto.getProductId())) {
            log.error("Product ID mismatch: {} vs {}", productId, dto.getProductId());
            throw new IllegalArgumentException("Product ID mismatch");
        }
        existingProduct = objectMapper.convertValue(dto, Product.class);
        existingProduct.setProductId(productId);
        existingProduct.setCategory(categoryService.getCategoryById(dto.getCategoryId()));
        log.info("Product updated successfully: {}", existingProduct);
        return ResponseEntity.ok(productService.updateProduct(existingProduct));
    }
}