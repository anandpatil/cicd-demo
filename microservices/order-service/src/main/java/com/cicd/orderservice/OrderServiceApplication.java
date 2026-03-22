package com.cicd.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.*;

@SpringBootApplication
@RestController
@RequestMapping("/api/orders")
public class OrderServiceApplication {

    private final Map<String, Order> orders = new HashMap<>();
    private int orderCounter = 100;

    public OrderServiceApplication() {
        orders.put("ORD001", new Order("ORD001", "1", Arrays.asList("PROD1", "PROD2"), 299.99));
        orders.put("ORD002", new Order("ORD002", "2", Arrays.asList("PROD3"), 49.99));
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        Order order = orders.get(id);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request) {
        String orderId = "ORD" + (++orderCounter);
        Order order = new Order(orderId, request.getUserId(), request.getProducts(), request.getTotal());
        orders.put(orderId, order);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "order-service");
        return status;
    }

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}

class Order {
    private String id;
    private String userId;
    private List<String> products;
    private double total;

    public Order() {}

    public Order(String id, String userId, List<String> products, double total) {
        this.id = id;
        this.userId = userId;
        this.products = products;
        this.total = total;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<String> getProducts() { return products; }
    public void setProducts(List<String> products) { this.products = products; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}

class OrderRequest {
    private String userId;
    private List<String> products;
    private double total;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<String> getProducts() { return products; }
    public void setProducts(List<String> products) { this.products = products; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
