package com.example.rentedapp.data.model;

public class User {
    private String id;
    private String email;
    private String fullName;
    private String role;
    private String phone;
    private String avatar;

    public String getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getPhone() { return phone; }
    public String getAvatar() { return avatar; }

    private String getBaseOrigin() {
        String baseUrl = "http://10.0.2.2:5000/api/v1/";
        try {
            baseUrl = com.example.rentedapp.data.network.ApiClient.BASE_URL;
        } catch (Exception e) {}
        if (baseUrl.contains("/api/v1/")) {
            return baseUrl.split("/api/v1/")[0];
        }
        return "http://10.0.2.2:5000";
    }

    public String getAbsoluteAvatarUrl() {
        if (avatar == null || avatar.isEmpty()) {
            return null;
        }
        if (avatar.startsWith("http")) {
            return avatar;
        }
        String origin = getBaseOrigin();
        return origin + (avatar.startsWith("/") ? "" : "/") + avatar;
    }
}
