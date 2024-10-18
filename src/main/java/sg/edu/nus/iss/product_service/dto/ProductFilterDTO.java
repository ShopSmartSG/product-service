package sg.edu.nus.iss.product_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductFilterDTO {
    private String pincode;
    private UUID categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String searchText;
    private Double rangeInKm;
}
