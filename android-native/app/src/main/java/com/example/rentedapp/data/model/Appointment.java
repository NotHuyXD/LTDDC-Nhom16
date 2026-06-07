package com.example.rentedapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Appointment {
    private String id;
    @SerializedName("room_id")
    private String roomId;
    @SerializedName("tenant_id")
    private String tenantId;
    @SerializedName("appointment_date")
    private String appointmentDate;
    @SerializedName("appointment_time")
    private String appointmentTime;
    private String message;
    private String status; // pending, confirmed, cancelled
    @SerializedName("created_at")
    private String createdAt;

    // Additional fields populated by JOIN queries in list endpoint:
    @SerializedName("room_title")
    private String roomTitle;
    @SerializedName("room_address")
    private String roomAddress;
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

    // Getters
    public String getId() { return id; }
    public String getRoomId() { return roomId; }
    public String getTenantId() { return tenantId; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getMessage() { return message; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }

    public String getRoomTitle() { return roomTitle; }
    public String getRoomAddress() { return roomAddress; }
    public String getRoomImage() { return roomImage; }
    public String getTenantName() { return tenantName; }
    public String getTenantPhone() { return tenantPhone; }
    public String getTenantEmail() { return tenantEmail; }
    public String getTenantAvatar() { return tenantAvatar; }
    public String getLandlordName() { return landlordName; }
    public String getLandlordPhone() { return landlordPhone; }
}
