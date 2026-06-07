package com.example.rentedapp.data.model;

public class AuthResponse {
    private String token;
    private User user;

    public String getAccessToken() { return token; }
    public User getUser() { return user; }
}
