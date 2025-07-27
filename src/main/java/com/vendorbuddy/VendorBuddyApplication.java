package com.vendorbuddy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.vendorbuddy.service.ProductLocationBackfillService;

import java.util.Arrays;

@SpringBootApplication
public class VendorBuddyApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendorBuddyApplication.class, args);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public CommandLineRunner backfillProductLocations(ProductLocationBackfillService backfillService) {
        return args -> {
            // Run this ONCE to backfill supplierLat and supplierLng for all products
            backfillService.backfillSupplierLocations();
            System.out.println("Product supplier locations backfilled.");
        };
    }
}
