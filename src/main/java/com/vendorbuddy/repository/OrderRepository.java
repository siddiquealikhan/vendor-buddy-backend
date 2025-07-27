package com.vendorbuddy.repository;

import com.vendorbuddy.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    
    @Query("{'vendorId': ?0}")
    Page<Order> findByVendorId(String vendorId, Pageable pageable);
    
    @Query("{'supplierId': ?0}")
    Page<Order> findBySupplierId(String supplierId, Pageable pageable);
    
    @Query("{'status': ?0}")
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    @Query("{'$and': [{'supplierId': ?0}, {'status': ?1}]}")
    Page<Order> findBySupplierIdAndStatus(String supplierId, Order.OrderStatus status, Pageable pageable);
    
    @Query("{'$and': [{'vendorId': ?0}, {'status': ?1}]}")
    Page<Order> findByVendorIdAndStatus(String vendorId, Order.OrderStatus status, Pageable pageable);
    
    @Query("{'createdAt': {$gte: ?0, $lte: ?1}}")
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{'$and': [{'supplierId': ?0}, {'createdAt': {$gte: ?1, $lte: ?2}}]}")
    List<Order> findBySupplierIdAndCreatedAtBetween(String supplierId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("{'productId': ?0}")
    List<Order> findByProductId(String productId);
}