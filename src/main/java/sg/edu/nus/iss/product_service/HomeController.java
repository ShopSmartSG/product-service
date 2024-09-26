package sg.edu.nus.iss.product_service;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public  class HomeController {
    @RequestMapping("/")
    public String home() {
        return "Welcome to Shopsmart Product Management";
    }
}

