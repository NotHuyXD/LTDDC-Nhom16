package com.example.rentedapp.data.model;

public class RegisterRequest {
    private String email;
    private String phone;
    private String password;
    private String fullName;
    private String role; // "tenant" or "landlord"

    public RegisterRequest(String email, String phone, String password, String fullName, String role) {
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }
}
