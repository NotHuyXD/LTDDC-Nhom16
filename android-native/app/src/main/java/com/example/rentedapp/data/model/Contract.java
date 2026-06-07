package com.example.rentedapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Contract {
    private String id;
    
    @SerializedName("room_id")
    private String roomId;
    
    @SerializedName("tenant_id")
    private String tenantId;
    
    @SerializedName("landlord_id")
    private String landlordId;
    
    @SerializedName("request_id")
    private String requestId;
    
    @SerializedName("start_date")
    private String startDate;
    
    @SerializedName("end_date")
    private String endDate;
    
    @SerializedName("monthly_rent")
    private double monthlyRent;
    
    @SerializedName("deposit_amount")
    private double depositAmount;
    
    private String terms;
    private String status; // pending_sign, active, expired, terminated
    
    @SerializedName("signed_at")
    private String signedAt;
    
    @SerializedName("created_at")
    private String createdAt;
    
    @SerializedName("updated_at")
    private String updatedAt;

    // Extra fields from join queries
    @SerializedName("room_title")
    private String roomTitle;
    
    @SerializedName("room_address")
    private String roomAddress;
    
    @SerializedName("room_price")
    private double roomPrice;
    
    @SerializedName("room_area")
    private double roomArea;
    
    @SerializedName("room_image")
    private String roomImage;
    
    @SerializedName("tenant_name")
    private String tenantName;
    
    @SerializedName("tenant_phone")
    private String tenantPhone;
    
    @SerializedName("tenant_email")
    private String tenantEmail;
    
    @SerializedName("tenant_avatar")
    private String tenantAvatar;
    
    @SerializedName("landlord_name")
    private String landlordName;
    
    @SerializedName("landlord_phone")
    private String landlordPhone;
    
    @SerializedName("landlord_email")
    private String landlordEmail;
    
    @SerializedName("landlord_avatar")
    private String landlordAvatar;

    // Getters
    public String getId() { return id; }
    public String getRoomId() { return roomId; }
    public String getTenantId() { return tenantId; }
    public String getLandlordId() { return landlordId; }
    public String getRequestId() { return requestId; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public double getMonthlyRent() { return monthlyRent; }
    public double getDepositAmount() { return depositAmount; }
    public String getTerms() { return terms; }
    public String getStatus() { return status; }
    public String getSignedAt() { return signedAt; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }

    public String getRoomTitle() { return roomTitle; }
    public String getRoomAddress() { return roomAddress; }
    public double getRoomPrice() { return roomPrice; }
    public double getRoomArea() { return roomArea; }
    public String getRoomImage() { return roomImage; }
    public String getTenantName() { return tenantName; }
    public String getTenantPhone() { return tenantPhone; }
    public String getTenantEmail() { return tenantEmail; }
    public String getTenantAvatar() { return tenantAvatar; }
    public String getLandlordName() { return landlordName; }
    public String getLandlordPhone() { return landlordPhone; }
    public String getLandlordEmail() { return landlordEmail; }
    public String getLandlordAvatar() { return landlordAvatar; }

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

    public String getAbsoluteRoomImageUrl() {
        if (roomImage == null || roomImage.isEmpty()) {
            return null;
        }
        if (roomImage.startsWith("http")) {
            return roomImage;
        }
        String origin = getBaseOrigin();
        return origin + (roomImage.startsWith("/") ? "" : "/") + roomImage;
    }
}
