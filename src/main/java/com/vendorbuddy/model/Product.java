package com.vendorbuddy.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.annotation.Transient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Document(collection = "products")
public class Product {

    @Id
    private String id;

    @NotBlank(message = "Product name is required")
    @TextIndexed
    private String name;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private Double unitPrice;

    @NotBlank(message = "Unit type is required")
    private String unitType; // kg, litre, piece, etc.

    @NotNull(message = "Stock quantity is required")
    @Positive(message = "Stock must be positive")
    private Integer stock;

    @NotNull(message = "Delivery range is required")
    @Positive(message = "Delivery range must be positive")
    private Integer deliveryRange; // in kilometers

    private String imageUrl;
    private String description;
    private Double supplierLat;
    private Double supplierLng;
    private Integer deliveryDays;
    private String supplierId;
    private Double rating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Transient
    private Double distanceKm;

    // Constructors
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.rating = 0.0;
        this.reviewCount = 0;
    }

    public Product(String name, String category, Double unitPrice, String unitType,
                   Integer stock, Integer deliveryRange, String supplierId) {
        this();
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.unitType = unitType;
        this.stock = stock;
        this.deliveryRange = deliveryRange;
        this.supplierId = supplierId;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getDeliveryRange() { return deliveryRange; }
    public void setDeliveryRange(Integer deliveryRange) { this.deliveryRange = deliveryRange; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getSupplierLat() { return supplierLat; }
    public void setSupplierLat(Double supplierLat) { this.supplierLat = supplierLat; }

    public Double getSupplierLng() { return supplierLng; }
    public void setSupplierLng(Double supplierLng) { this.supplierLng = supplierLng; }

    public Integer getDeliveryDays() { return deliveryDays; }
    public void setDeliveryDays(Integer deliveryDays) { this.deliveryDays = deliveryDays; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
}