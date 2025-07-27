package com.vendorbuddy.repository;

import com.vendorbuddy.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("{'role': ?0}")
    java.util.List<User> findByRole(User.UserRole role);
    
    @Query("{'location.latitude': {$gte: ?0, $lte: ?1}, 'location.longitude': {$gte: ?2, $lte: ?3}}")
    java.util.List<User> findByLocationWithin(double minLat, double maxLat, double minLng, double maxLng);
}