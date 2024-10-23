package sg.edu.nus.iss.product_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sg.edu.nus.iss.product_service.dto.ProductFilterDTO;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    @PostMapping("/filter")
    @Operation(summary = "Filtered products")
    public ResponseEntity<List<Product>> filterProducts(@RequestBody ProductFilterDTO filterDTO) {
        List<Product> filteredProducts = productService.getFilteredProducts(filterDTO);
        log.info("Filtered Products: {}", filteredProducts);
        return ResponseEntity.ok(filteredProducts);
    }
}
