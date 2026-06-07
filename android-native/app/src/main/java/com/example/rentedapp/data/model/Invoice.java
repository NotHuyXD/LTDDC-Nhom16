package com.example.rentedapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Invoice {
    private String id;
    
    @SerializedName("contract_id")
    private String contractId;
    
    @SerializedName("utility_reading_id")
    private String utilityReadingId;
    
    @SerializedName("period_month")
    private String periodMonth;
    
    @SerializedName("base_rent")
    private double baseRent;
    
    @SerializedName("electric_usage")
    private double electricUsage;
    
    @SerializedName("water_usage")
    private double waterUsage;
    
    @SerializedName("electric_fee")
    private double electricFee;
    
    @SerializedName("water_fee")
    private double waterFee;
    
    @SerializedName("other_fees")
    private double otherFees;
    
    private double total;
    
    @SerializedName("due_date")
    private String dueDate;
    
    private String status; // unpaid, paid, overdue, disputed
    
    @SerializedName("paid_at")
    private String paidAt;
    
    @SerializedName("created_at")
    private String createdAt;

    // JOIN fields
    @SerializedName("room_id")
    private String roomId;
    
    @SerializedName("room_title")
    private String roomTitle;
    
    @SerializedName("room_address")
    private String roomAddress;
    
    @SerializedName("tenant_name")
    private String tenantName;
    
    @SerializedName("landlord_name")
    private String landlordName;

    @SerializedName("tenant_id")
    private String tenantId;

    @SerializedName("landlord_id")
    private String landlordId;

    // Getters
    public String getId() { return id; }
    public String getContractId() { return contractId; }
    public String getUtilityReadingId() { return utilityReadingId; }
    public String getPeriodMonth() { return periodMonth; }
    public double getBaseRent() { return baseRent; }
    public double getElectricUsage() { return electricUsage; }
    public double getWaterUsage() { return waterUsage; }
    public double getElectricFee() { return electricFee; }
    public double getWaterFee() { return waterFee; }
    public double getOtherFees() { return otherFees; }
    public double getTotal() { return total; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public String getPaidAt() { return paidAt; }
    public String getCreatedAt() { return createdAt; }

    public String getRoomId() { return roomId; }
    public String getRoomTitle() { return roomTitle; }
    public String getRoomAddress() { return roomAddress; }
    public String getTenantName() { return tenantName; }
    public String getLandlordName() { return landlordName; }
    public String getTenantId() { return tenantId; }
    public String getLandlordId() { return landlordId; }
}
