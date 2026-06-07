package com.example.rentedapp.data.model;

public class UpdateProfileRequest {
    private String fullName;
    private String phone;

    public UpdateProfileRequest(String fullName, String phone) {
        this.fullName = fullName;
        this.phone = phone;
    }

    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
}
