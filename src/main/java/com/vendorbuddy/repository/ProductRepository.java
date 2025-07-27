package com.vendorbuddy.repository;

import com.vendorbuddy.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    
    @Query("{'$text': {'$search': ?0}}")
    Page<Product> findByTextSearch(String searchTerm, Pageable pageable);
    
    @Query("{'category': ?0}")
    Page<Product> findByCategory(String category, Pageable pageable);
    
    @Query("{'supplierId': ?0}")
    List<Product> findBySupplierId(String supplierId);
    
    @Query("{'unitPrice': {$gte: ?0, $lte: ?1}}")
    Page<Product> findByPriceRange(Double minPrice, Double maxPrice, Pageable pageable);
    
    @Query("{'$and': [{'$text': {'$search': ?0}}, {'unitPrice': {$gte: ?1, $lte: ?2}}]}")
    Page<Product> findByTextSearchAndPriceRange(String searchTerm, Double minPrice, Double maxPrice, Pageable pageable);
    
    @Query("{'$and': [{'category': ?0}, {'unitPrice': {$gte: ?1, $lte: ?2}}]}")
    Page<Product> findByCategoryAndPriceRange(String category, Double minPrice, Double maxPrice, Pageable pageable);
    
    @Query("{'stock': {$gt: 0}}")
    Page<Product> findAvailableProducts(Pageable pageable);
}