package com.cicd.inventoryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/api/inventory")
public class InventoryServiceApplication {

    private final Map<String, Product> inventory = new HashMap<>();

    public InventoryServiceApplication() {
        inventory.put("PROD1", new Product("PROD1", "Laptop", 10, 999.99));
        inventory.put("PROD2", new Product("PROD2", "Mouse", 50, 29.99));
        inventory.put("PROD3", new Product("PROD3", "Keyboard", 30, 79.99));
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return new ArrayList<>(inventory.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        Product product = inventory.get(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/check")
    public ResponseEntity<Map<String, Object>> checkAvailability(@RequestBody Map<String, String> request) {
        String productId = request.get("productId");
        int requestedQty = Integer.parseInt(request.get("quantity"));
        
        Map<String, Object> response = new HashMap<>();
        Product product = inventory.get(productId);
        
        if (product == null) {
            response.put("available", false);
            response.put("message", "Product not found");
            return ResponseEntity.badRequest().body(response);
        }
        
        boolean available = product.getQuantity() >= requestedQty;
        response.put("available", available);
        response.put("productId", productId);
        response.put("requestedQuantity", requestedQty);
        response.put("availableQuantity", product.getQuantity());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "inventory-service");
        return status;
    }

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}

class Product {
    private String id;
    private String name;
    private int quantity;
    private double price;

    public Product() {}

    public Product(String id, String name, int quantity, double price) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
