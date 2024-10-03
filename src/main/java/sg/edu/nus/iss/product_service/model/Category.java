package sg.edu.nus.iss.product_service.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;
import java.util.List;

@Entity
@Data
public class Category {

    @Id
    @GeneratedValue
    private UUID categoryId;

    private String categoryName;

    private String categoryDescription;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;  // One category can have many products
}
