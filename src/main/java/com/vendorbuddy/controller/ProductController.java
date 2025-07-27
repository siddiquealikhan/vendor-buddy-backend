package com.vendorbuddy.controller;

import com.vendorbuddy.model.Product;
import com.vendorbuddy.model.User;
import com.vendorbuddy.service.ProductService;
import com.vendorbuddy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double locationKms,
            @RequestParam(required = false) Double userLat,
            @RequestParam(required = false) Double userLng) {

        try {
            Page<Product> products;
            
            if (search != null || category != null || minPrice != null || maxPrice != null || locationKms != null || (userLat != null && userLng != null)) {
                products = productService.searchProducts(search, minPrice, maxPrice, category, locationKms, userLat, userLng, page, size, sortBy, sortDir);
            } else {
                products = productService.getAllProducts(page, size, sortBy, sortDir);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("products", products.getContent());
            response.put("currentPage", products.getNumber());
            response.put("totalItems", products.getTotalElements());
            response.put("totalPages", products.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable String id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody Product product) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getCurrentUser(email);
            if (user.getRole() != User.UserRole.SUPPLIER) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Only suppliers can create products");
                return ResponseEntity.badRequest().body(error);
            }
            product.setSupplierId(user.getId());
            // Set supplier location from user profile
            if (user.getLocation() != null) {
                product.setSupplierLat(user.getLocation().getLatitude());
                product.setSupplierLng(user.getLocation().getLongitude());
            }
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.ok(createdProduct);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @RequestBody Product productDetails) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getCurrentUser(email);
            
            if (user.getRole() != User.UserRole.SUPPLIER) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Only suppliers can update products");
                return ResponseEntity.badRequest().body(error);
            }
            
            Product updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getCurrentUser(email);
            
            if (user.getRole() != User.UserRole.SUPPLIER) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Only suppliers can delete products");
                return ResponseEntity.badRequest().body(error);
            }
            
            productService.deleteProduct(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<?> getProductsBySupplier(@PathVariable String supplierId) {
        try {
            List<Product> products = productService.getProductsBySupplier(supplierId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}