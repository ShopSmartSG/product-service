package sg.edu.nus.iss.product_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CategoryDTO {
    private UUID categoryId;
    @NotBlank(message = "Category name is mandatory")
    private String categoryName;
}
