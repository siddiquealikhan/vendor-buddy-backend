package com.vendorbuddy.service;

import com.vendorbuddy.model.Order;
import com.vendorbuddy.model.Product;
import com.vendorbuddy.model.User;
import com.vendorbuddy.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    public Order createOrder(Order order) {
        // Validate product exists and has sufficient stock
        Product product = productService.getProductById(order.getProductId());
        if (product.getStock() < order.getQuantity()) {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
        
        // Calculate total amount
        double totalAmount = product.getUnitPrice() * order.getQuantity();
        order.setTotalAmount(totalAmount);
        
        // Set estimated delivery time (mock: 2-4 hours from now)
        LocalDateTime estimatedDelivery = LocalDateTime.now().plusHours(2 + (int)(Math.random() * 3));
        order.setEstimatedDeliveryTime(estimatedDelivery);
        
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }
    
    public Page<Order> getOrdersByUser(String userId, String userRole, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        System.out.println("[OrderService] getOrdersByUser: userId=" + userId + ", userRole=" + userRole);
        Page<Order> result;
        if ("VENDOR".equals(userRole)) {
            result = orderRepository.findByVendorId(userId, pageable);
        } else if ("SUPPLIER".equals(userRole)) {
            result = orderRepository.findBySupplierId(userId, pageable);
        } else {
            throw new RuntimeException("Invalid user role");
        }
        System.out.println("[OrderService] Orders found: " + result.getTotalElements());
        return result;
    }
    
    public Page<Order> getOrdersByStatus(String userId, String userRole, Order.OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        if ("VENDOR".equals(userRole)) {
            return orderRepository.findByVendorIdAndStatus(userId, status, pageable);
        } else if ("SUPPLIER".equals(userRole)) {
            return orderRepository.findBySupplierIdAndStatus(userId, status, pageable);
        } else {
            throw new RuntimeException("Invalid user role");
        }
    }
    
    public Order updateOrderStatus(String orderId, Order.OrderStatus status, String userId, String userRole) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        // Validate that the user can update this order
        if ("SUPPLIER".equals(userRole) && !order.getSupplierId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this order");
        }
        if ("VENDOR".equals(userRole) && !order.getVendorId().equals(userId)) {
            throw new RuntimeException("Unauthorized to update this order");
        }
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        // If order is accepted, update stock
        if (status == Order.OrderStatus.ACCEPTED) {
            productService.updateStock(order.getProductId(), order.getQuantity());
        }
        
        // If order is delivered, set actual delivery time
        if (status == Order.OrderStatus.DELIVERED) {
            order.setActualDeliveryTime(LocalDateTime.now());
        }
        
        return orderRepository.save(order);
    }
    
    public Order getOrderById(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    public Map<String, Object> getAnalytics(String supplierId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        List<Order> recentOrders = orderRepository.findBySupplierIdAndCreatedAtBetween(
            supplierId, oneWeekAgo, LocalDateTime.now());
        
        // Mock analytics data
        Map<String, Object> analytics = new java.util.HashMap<>();
        analytics.put("totalOrders", recentOrders.size());
        analytics.put("totalRevenue", recentOrders.stream()
            .mapToDouble(Order::getTotalAmount)
            .sum());
        analytics.put("pendingOrders", recentOrders.stream()
            .filter(order -> order.getStatus() == Order.OrderStatus.PENDING)
            .count());
        analytics.put("completedOrders", recentOrders.stream()
            .filter(order -> order.getStatus() == Order.OrderStatus.DELIVERED)
            .count());
        
        // Orders per day for the last week
        Map<String, Long> ordersPerDay = recentOrders.stream()
            .collect(Collectors.groupingBy(
                order -> order.getCreatedAt().toLocalDate().toString(),
                Collectors.counting()
            ));
        analytics.put("ordersPerDay", ordersPerDay);
        
        return analytics;
    }
    
    public List<Order> getOrdersByProduct(String productId) {
        return orderRepository.findByProductId(productId);
    }
}