package com.vendorbuddy.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {
    
    @GetMapping("/demand-trends")
    public ResponseEntity<?> getDemandTrends() {
        try {
            // Mock demand analytics data
            Map<String, Object> analytics = new HashMap<>();
            
            // Weekly demand data for different products
            Map<String, List<Integer>> weeklyDemand = new HashMap<>();
            weeklyDemand.put("Onions", Arrays.asList(150, 180, 200, 175, 190, 210, 195));
            weeklyDemand.put("Tomatoes", Arrays.asList(200, 220, 250, 230, 240, 260, 245));
            weeklyDemand.put("Potatoes", Arrays.asList(300, 320, 350, 330, 340, 360, 345));
            weeklyDemand.put("Oil", Arrays.asList(100, 120, 140, 130, 135, 150, 145));
            weeklyDemand.put("Spices", Arrays.asList(80, 90, 100, 95, 105, 110, 108));
            
            analytics.put("weeklyDemand", weeklyDemand);
            
            // Top selling products
            List<Map<String, Object>> topProducts = Arrays.asList(
                Map.of("name", "Onions", "quantity", 1300, "revenue", 65000.0),
                Map.of("name", "Tomatoes", "quantity", 1645, "revenue", 82300.0),
                Map.of("name", "Potatoes", "quantity", 2345, "revenue", 117250.0),
                Map.of("name", "Oil", "quantity", 920, "revenue", 92000.0),
                Map.of("name", "Spices", "quantity", 688, "revenue", 34400.0)
            );
            
            analytics.put("topProducts", topProducts);
            
            // Revenue trends
            List<Map<String, Object>> revenueTrends = Arrays.asList(
                Map.of("date", LocalDate.now().minusDays(6).toString(), "revenue", 45000.0),
                Map.of("date", LocalDate.now().minusDays(5).toString(), "revenue", 52000.0),
                Map.of("date", LocalDate.now().minusDays(4).toString(), "revenue", 48000.0),
                Map.of("date", LocalDate.now().minusDays(3).toString(), "revenue", 55000.0),
                Map.of("date", LocalDate.now().minusDays(2).toString(), "revenue", 51000.0),
                Map.of("date", LocalDate.now().minusDays(1).toString(), "revenue", 58000.0),
                Map.of("date", LocalDate.now().toString(), "revenue", 54000.0)
            );
            
            analytics.put("revenueTrends", revenueTrends);
            
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    @GetMapping("/price-predictions")
    public ResponseEntity<?> getPricePredictions() {
        try {
            // Mock price prediction data
            Map<String, Object> predictions = new HashMap<>();
            
            List<Map<String, Object>> productPredictions = Arrays.asList(
                Map.of(
                    "productName", "Onions",
                    "currentPrice", 50.0,
                    "predictedPrice", 52.5,
                    "trend", "increasing",
                    "confidence", 85
                ),
                Map.of(
                    "productName", "Tomatoes",
                    "currentPrice", 40.0,
                    "predictedPrice", 38.0,
                    "trend", "decreasing",
                    "confidence", 78
                ),
                Map.of(
                    "productName", "Potatoes",
                    "currentPrice", 45.0,
                    "predictedPrice", 47.0,
                    "trend", "increasing",
                    "confidence", 92
                ),
                Map.of(
                    "productName", "Oil",
                    "currentPrice", 100.0,
                    "predictedPrice", 98.0,
                    "trend", "decreasing",
                    "confidence", 67
                ),
                Map.of(
                    "productName", "Spices",
                    "currentPrice", 60.0,
                    "predictedPrice", 62.0,
                    "trend", "increasing",
                    "confidence", 88
                )
            );
            
            predictions.put("predictions", productPredictions);
            predictions.put("lastUpdated", LocalDate.now().toString());
            
            return ResponseEntity.ok(predictions);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}