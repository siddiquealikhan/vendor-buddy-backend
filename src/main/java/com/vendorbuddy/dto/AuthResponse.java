package com.vendorbuddy.dto;

import com.vendorbuddy.model.User;

public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private User user;
    private String message;
    
    public AuthResponse() {}
    
    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
    
    public AuthResponse(String token, User user, String message) {
        this.token = token;
        this.user = user;
        this.message = message;
    }
    
    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}