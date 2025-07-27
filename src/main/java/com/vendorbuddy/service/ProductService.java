package com.vendorbuddy.service;

import com.vendorbuddy.model.Product;
import com.vendorbuddy.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Page<Product> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findAll(pageable);
    }
    
    public Page<Product> searchProducts(String searchTerm, Double minPrice, Double maxPrice, 
                                      String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            if (minPrice != null && maxPrice != null) {
                return productRepository.findByTextSearchAndPriceRange(searchTerm, minPrice, maxPrice, pageable);
            } else {
                return productRepository.findByTextSearch(searchTerm, pageable);
            }
        } else if (category != null && !category.trim().isEmpty()) {
            if (minPrice != null && maxPrice != null) {
                return productRepository.findByCategoryAndPriceRange(category, minPrice, maxPrice, pageable);
            } else {
                return productRepository.findByCategory(category, pageable);
            }
        } else if (minPrice != null && maxPrice != null) {
            return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
        } else {
            return productRepository.findAvailableProducts(pageable);
        }
    }
    
    public Page<Product> searchProducts(String searchTerm, Double minPrice, Double maxPrice, String category, Double locationKms, Double userLat, Double userLng, int page, int size, String sortBy, String sortDir) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy != null ? sortBy : "name").ascending());
        List<Product> filtered = new ArrayList<>();
        List<Product> all = productRepository.findAll();
        for (Product p : all) {
            boolean matches = true;
            if (searchTerm != null && !searchTerm.trim().isEmpty() && !p.getName().toLowerCase().contains(searchTerm.toLowerCase())) matches = false;
            if (category != null && !category.trim().isEmpty() && !category.equalsIgnoreCase(p.getCategory())) matches = false;
            if (minPrice != null && p.getUnitPrice() < minPrice) matches = false;
            if (maxPrice != null && p.getUnitPrice() > maxPrice) matches = false;
            if (userLat != null && userLng != null && p.getSupplierLat() != null && p.getSupplierLng() != null && locationKms != null) {
                double dist = haversine(userLat, userLng, p.getSupplierLat(), p.getSupplierLng());
                if (dist > locationKms) matches = false;
            }
            if (matches) filtered.add(p);
        }
        // Attach distance and deliveryDays if user location is provided
        if (userLat != null && userLng != null) {
            for (Product p : filtered) {
                if (p.getSupplierLat() != null && p.getSupplierLng() != null) {
                    double dist = haversine(userLat, userLng, p.getSupplierLat(), p.getSupplierLng());
                    p.setDistanceKm(dist);
                    int days = (int) Math.ceil(dist / 50.0);
                    if (days < 1) days = 1;
                    p.setDeliveryDays(days);
                }
            }
        }
        // Sorting
        filtered.sort((a, b) -> {
            if ("price_asc".equals(sortBy)) return Double.compare(a.getUnitPrice(), b.getUnitPrice());
            if ("price_desc".equals(sortBy)) return Double.compare(b.getUnitPrice(), a.getUnitPrice());
            if ("name_asc".equals(sortBy)) return a.getName().compareToIgnoreCase(b.getName());
            if ("name_desc".equals(sortBy)) return b.getName().compareToIgnoreCase(a.getName());
            if ("distance_asc".equals(sortBy) && userLat != null && userLng != null && a.getSupplierLat() != null && b.getSupplierLat() != null) {
                double da = haversine(userLat, userLng, a.getSupplierLat(), a.getSupplierLng());
                double db = haversine(userLat, userLng, b.getSupplierLat(), b.getSupplierLng());
                return Double.compare(da, db);
            }
            return 0;
        });
        int start = Math.min(page * size, filtered.size());
        int end = Math.min(start + size, filtered.size());
        return new PageImpl<>(filtered.subList(start, end), pageable, filtered.size());
    }
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
    
    public Product updateProduct(String id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (productDetails.getName() != null) {
            product.setName(productDetails.getName());
        }
        if (productDetails.getCategory() != null) {
            product.setCategory(productDetails.getCategory());
        }
        if (productDetails.getUnitPrice() != null) {
            product.setUnitPrice(productDetails.getUnitPrice());
        }
        if (productDetails.getUnitType() != null) {
            product.setUnitType(productDetails.getUnitType());
        }
        if (productDetails.getStock() != null) {
            product.setStock(productDetails.getStock());
        }
        if (productDetails.getDeliveryRange() != null) {
            product.setDeliveryRange(productDetails.getDeliveryRange());
        }
        if (productDetails.getImageUrl() != null) {
            product.setImageUrl(productDetails.getImageUrl());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }
    
    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    public List<Product> getProductsBySupplier(String supplierId) {
        return productRepository.findBySupplierId(supplierId);
    }
    
    public void updateStock(String productId, Integer quantity) {
        Product product = getProductById(productId);
        int newStock = product.getStock() - quantity;
        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        product.setStock(newStock);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);
    }
}