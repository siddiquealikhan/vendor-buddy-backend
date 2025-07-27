package com.vendorbuddy.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/price-trends")
@CrossOrigin(origins = "*")
public class PriceTrendsController {
    
    @GetMapping("/{productId}")
    public ResponseEntity<?> getPriceTrends(@PathVariable String productId) {
        try {
            // Mock price trends data for different products
            Map<String, Object> trends = new HashMap<>();
            
            // Generate 30 days of historical price data
            List<Map<String, Object>> priceHistory = new ArrayList<>();
            LocalDate startDate = LocalDate.now().minusDays(30);
            
            // Different price patterns for different products
            double basePrice = getBasePrice(productId);
            double volatility = getVolatility(productId);
            
            for (int i = 0; i < 30; i++) {
                LocalDate date = startDate.plusDays(i);
                double price = basePrice + (Math.random() - 0.5) * volatility;
                price = Math.max(price, basePrice * 0.7); // Minimum 70% of base price
                
                priceHistory.add(Map.of(
                    "date", date.toString(),
                    "price", Math.round(price * 100.0) / 100.0,
                    "volume", (int)(Math.random() * 1000) + 500
                ));
            }
            
            trends.put("productId", productId);
            trends.put("productName", getProductName(productId));
            trends.put("priceHistory", priceHistory);
            trends.put("currentPrice", priceHistory.get(priceHistory.size() - 1).get("price"));
            trends.put("averagePrice", calculateAveragePrice(priceHistory));
            trends.put("priceChange", calculatePriceChange(priceHistory));
            
            return ResponseEntity.ok(trends);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/compare/{productIds}")
    public ResponseEntity<?> comparePriceTrends(@PathVariable String productIds) {
        try {
            String[] ids = productIds.split(",");
            Map<String, Object> comparison = new HashMap<>();
            
            for (String productId : ids) {
                Map<String, Object> productTrends = new HashMap<>();
                List<Map<String, Object>> priceHistory = new ArrayList<>();
                LocalDate startDate = LocalDate.now().minusDays(30);
                
                double basePrice = getBasePrice(productId);
                double volatility = getVolatility(productId);
                
                for (int i = 0; i < 30; i++) {
                    LocalDate date = startDate.plusDays(i);
                    double price = basePrice + (Math.random() - 0.5) * volatility;
                    price = Math.max(price, basePrice * 0.7);
                    
                    priceHistory.add(Map.of(
                        "date", date.toString(),
                        "price", Math.round(price * 100.0) / 100.0
                    ));
                }
                
                productTrends.put("productName", getProductName(productId));
                productTrends.put("priceHistory", priceHistory);
                productTrends.put("currentPrice", priceHistory.get(priceHistory.size() - 1).get("price"));
                
                comparison.put(productId, productTrends);
            }
            
            return ResponseEntity.ok(comparison);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    private double getBasePrice(String productId) {
        // Mock base prices for different products
        Map<String, Double> basePrices = Map.of(
            "onions", 50.0,
            "tomatoes", 40.0,
            "potatoes", 45.0,
            "oil", 100.0,
            "spices", 60.0
        );
        return basePrices.getOrDefault(productId.toLowerCase(), 50.0);
    }
    
    private double getVolatility(String productId) {
        // Mock volatility for different products
        Map<String, Double> volatility = Map.of(
            "onions", 10.0,
            "tomatoes", 15.0,
            "potatoes", 8.0,
            "oil", 20.0,
            "spices", 12.0
        );
        return volatility.getOrDefault(productId.toLowerCase(), 10.0);
    }
    
    private String getProductName(String productId) {
        Map<String, String> productNames = Map.of(
            "onions", "Onions",
            "tomatoes", "Tomatoes",
            "potatoes", "Potatoes",
            "oil", "Cooking Oil",
            "spices", "Spices"
        );
        return productNames.getOrDefault(productId.toLowerCase(), "Unknown Product");
    }
    
    private double calculateAveragePrice(List<Map<String, Object>> priceHistory) {
        return priceHistory.stream()
            .mapToDouble(entry -> (Double) entry.get("price"))
            .average()
            .orElse(0.0);
    }
    
    private double calculatePriceChange(List<Map<String, Object>> priceHistory) {
        if (priceHistory.size() < 2) return 0.0;
        
        double firstPrice = (Double) priceHistory.get(0).get("price");
        double lastPrice = (Double) priceHistory.get(priceHistory.size() - 1).get("price");
        
        return Math.round(((lastPrice - firstPrice) / firstPrice * 100) * 100.0) / 100.0;
    }
}