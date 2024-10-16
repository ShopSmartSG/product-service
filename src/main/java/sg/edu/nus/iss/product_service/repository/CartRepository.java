package sg.edu.nus.iss.product_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sg.edu.nus.iss.product_service.model.Cart;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
    Cart findByUserId(UUID userId);
}