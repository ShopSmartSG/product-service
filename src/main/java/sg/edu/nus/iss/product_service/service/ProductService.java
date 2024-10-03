package sg.edu.nus.iss.product_service.service;

import sg.edu.nus.iss.product_service.dto.ProductFilterDTO;
import sg.edu.nus.iss.product_service.model.Product;
import sg.edu.nus.iss.product_service.repository.ProductRepository;
import sg.edu.nus.iss.product_service.service.strategy.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

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
