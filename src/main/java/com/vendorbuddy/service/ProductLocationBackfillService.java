package com.vendorbuddy.service;

import com.vendorbuddy.model.Product;
import com.vendorbuddy.model.User;
import com.vendorbuddy.repository.ProductRepository;
import com.vendorbuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductLocationBackfillService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    public void backfillSupplierLocations() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            if (product.getSupplierLat() == null || product.getSupplierLng() == null) {
                User supplier = userRepository.findById(product.getSupplierId()).orElse(null);
                if (supplier != null && supplier.getLocation() != null) {
                    product.setSupplierLat(supplier.getLocation().getLatitude());
                    product.setSupplierLng(supplier.getLocation().getLongitude());
                    productRepository.save(product);
                }
            }
        }
    }
}

