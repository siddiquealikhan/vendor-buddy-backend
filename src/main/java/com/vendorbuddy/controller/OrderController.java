package com.vendorbuddy.controller;

import com.vendorbuddy.model.Order;
import com.vendorbuddy.model.User;
import com.vendorbuddy.service.OrderService;
import com.vendorbuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getCurrentUser(email);
            
            Page<Order> orders;
            if (status != null) {
                Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                orders = orderService.getOrdersByStatus(user.getId(), user.getRole().name(), orderStatus, page, size);
            } else {
                orders = orderService.getOrdersByUser(user.getId(), user.getRole().name(), page, size);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders.getContent());
            response.put("currentPage", orders.getNumber());
            response.put("totalItems", orders.getTotalElements());
            response.put("totalPages", orders.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody Order order) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getCurrentUser(email);
            
            if (user.getRole() != User.UserRole.VENDOR) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Only vendors can create orders");
                return ResponseEntity.badRequest().body(error);
            }
            
            order.setVendorId(user.getId());
            Order createdOrder = orderService.createOrder(order);
            return ResponseEntity.ok(createdOrder);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String id,
            @RequestParam String status) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getCurrentUser(email);
            
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            Order updatedOrder = orderService.updateOrderStatus(id, orderStatus, user.getId(), user.getRole().name());
            
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getCurrentUser(email);
            
            if (user.getRole() != User.UserRole.SUPPLIER) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Only suppliers can view analytics");
                return ResponseEntity.badRequest().body(error);
            }
            
            Map<String, Object> analytics = orderService.getAnalytics(user.getId());
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}