package sg.edu.nus.iss.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import sg.edu.nus.iss.product_service.utility.S3Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/merchants/products/")
@Tag(name = "Products", description = "Manage products in Shopsmart Application")
public class MerchantProductController {
    private final ProductService productService;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;
    private final CategoryService categoryService;

    @Autowired
    public MerchantProductController(ProductService productService, ObjectMapper objectMapper, S3Service s3Service, CategoryService categoryService) {
        this.productService = productService;
        this.objectMapper = objectMapper;
        this.s3Service = s3Service;
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Retrieve all products")
    public ResponseEntity<?> getAllProducts(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getAllProducts(pageable);
            return ResponseEntity.ok(products);
        } else {
            List<Product> products = productService.getAllProducts();
            return ResponseEntity.ok(products);
        }
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Retrieve product By ID")
    public ResponseEntity<?> getProductById(@PathVariable UUID productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        return ResponseEntity.ok(product);
    }

    @GetMapping("/{merchantId}")
    @Operation(summary = "Retrieve products by merchant ID")
    public ResponseEntity<?> getProductsByMerchantId(@PathVariable UUID merchantId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProductsByMerchantId(merchantId, pageable);
            return ResponseEntity.ok(products);
        } else {
            List<Product> products = productService.getProductsByMerchantId(merchantId);
            return ResponseEntity.ok(products);
        }
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Retrieve products by category ID")
    public ResponseEntity<?> getProductsByCategoryId(@PathVariable UUID categoryId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProductsByCategoryId(categoryId, pageable);
            return ResponseEntity.ok(products);
        } else {
            List<Product> products = productService.getProductsByCategoryId(categoryId);
            return ResponseEntity.ok(products);
        }
    }

    @GetMapping("/{merchantId}/category/{categoryId}")
    @Operation(summary = "Retrieve products by merchant ID and category ID")
    public ResponseEntity<?> getProductByMerchantIdAndCategoryId(@PathVariable UUID merchantId, @PathVariable UUID categoryId, @RequestParam(required = false) Integer page, @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId, pageable);
            return ResponseEntity.ok(products);
        } else {
            List<Product> products = productService.getProductsByMerchantIdAndCategoryId(merchantId, categoryId);
            return ResponseEntity.ok(products);
        }
    }

    @PostMapping
    @Operation(summary = "Add a new product")
    public ResponseEntity<?> addProduct( @Valid @RequestBody Product product) {
        // check if category exists
        // if it doesn't ask merchant to create category first
        Category category = categoryService.getCategoryByName(product.getCategory().getCategoryName());
        if (category == null) {
            throw new ResourceNotFoundException("Category not found , Please create category first");
        }
        product.setCategory(category);
        Product newProduct = productService.addProduct(product);
        return ResponseEntity.ok(newProduct);
    }

    @PostMapping("/upload")
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
        String fileName = s3Service.uploadFile(file);
        String fileUrl = s3Service.getFileUrl(fileName);
        return ResponseEntity.ok(fileUrl);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product by Product ID")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID productId) {
        // get existing data and delete it
        Product existingProduct = productService.getProductById(productId);
        if (existingProduct == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        productService.deleteProduct(existingProduct);
        return ResponseEntity.ok("Delete: successful");
    }

    @PutMapping("/{productId}")
    @Operation(summary = "Update product")
    public ResponseEntity<?> updateProduct(@PathVariable UUID productId,@Valid @RequestBody ProductDTO dto) {
        Product existingProduct = productService.getProductById(productId);
        if (existingProduct == null) {
            throw new ResourceNotFoundException("Product not found");
        }

        if (!existingProduct.getProductId().equals(dto.getProductId())) {
            throw new IllegalArgumentException("Product ID mismatch");
        }
        existingProduct = objectMapper.convertValue(dto, Product.class);
        existingProduct.setProductId(productId);
        existingProduct.setCategory(categoryService.getCategoryById(dto.getCategoryId()));
        return  ResponseEntity.ok(productService.updateProduct(existingProduct));
    }
}