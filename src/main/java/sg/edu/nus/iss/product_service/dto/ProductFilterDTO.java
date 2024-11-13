package sg.edu.nus.iss.product_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductFilterDTO {
    private UUID categoryId;
    private BigDecimal maxPrice;
    private BigDecimal minPrice;
    private String pincode;
    private Double rangeInKm;
    private String searchText;
}
