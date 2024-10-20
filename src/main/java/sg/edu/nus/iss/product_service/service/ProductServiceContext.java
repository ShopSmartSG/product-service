package sg.edu.nus.iss.product_service.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import sg.edu.nus.iss.product_service.service.strategy.AdminProductStrategy;
import sg.edu.nus.iss.product_service.service.strategy.MerchantProductStrategy;
import sg.edu.nus.iss.product_service.service.strategy.ProductStrategy;

@Component
public class ProductServiceContext {

    private ProductStrategy productStrategy;

    @Autowired
    private AdminProductStrategy adminProductStrategy;

    @Autowired
    private MerchantProductStrategy merchantProductStrategy;

    public void setProductStrategy(String role) {
        if (role.equalsIgnoreCase("admin")) {
            this.productStrategy = adminProductStrategy;
        } else if (role.equalsIgnoreCase("merchant")) {
            this.productStrategy = merchantProductStrategy;
        } else {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    public ProductStrategy getProductStrategy() {
        return productStrategy;
    }
}
