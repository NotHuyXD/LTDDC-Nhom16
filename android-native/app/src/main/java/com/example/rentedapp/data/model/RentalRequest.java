package com.example.rentedapp.data.model;

import com.google.gson.annotations.SerializedName;

public class RentalRequest {
    private String id;
    @SerializedName("room_id")
    private String roomId;
    @SerializedName("tenant_id")
    private String tenantId;
    private String message;
    @SerializedName("move_in_date")
    private String moveInDate;
    @SerializedName("num_people")
    private int numPeople;
    private String status; // pending, accepted, rejected, cancelled
    @SerializedName("contract_id")
    private String contractId;
    @SerializedName("created_at")
    private String createdAt;

    // Extra fields populated by JOINs
    @SerializedName("room_title")
    private String roomTitle;
    @SerializedName("room_address")
    private String roomAddress;
    @SerializedName("room_price")
    private double roomPrice;
    @SerializedName("room_image")
    private String roomImage;
    @SerializedName("tenant_name")
    private String tenantName;
    @SerializedName("tenant_avatar")
    private String tenantAvatar;
    @SerializedName("tenant_phone")
    private String tenantPhone;
    @SerializedName("landlord_name")
    private String landlordName;

    // Getters
    public String getId() { return id; }
    public String getRoomId() { return roomId; }
    public String getTenantId() { return tenantId; }
    public String getMessage() { return message; }
    public String getMoveInDate() { return moveInDate; }
    public int getNumPeople() { return numPeople; }
    public String getStatus() { return status; }
    public String getContractId() { return contractId; }
    public String getCreatedAt() { return createdAt; }

    public String getRoomTitle() { return roomTitle; }
    public String getRoomAddress() { return roomAddress; }
    public double getRoomPrice() { return roomPrice; }
    public String getRoomImage() { return roomImage; }
    public String getTenantName() { return tenantName; }
    public String getTenantAvatar() { return tenantAvatar; }
    public String getTenantPhone() { return tenantPhone; }
    public String getLandlordName() { return landlordName; }
}
