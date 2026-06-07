package com.example.rentedapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Payment {
    private String id;
    
    @SerializedName("invoice_id")
    private String invoiceId;
    
    @SerializedName("tenant_id")
    private String tenantId;
    
    private double amount;
    private String method; // cash, bank_transfer, momo, vnpay, zalopay
    
    @SerializedName("transaction_id")
    private String transactionId;
    
    private String status; // pending, success, failed
    
    @SerializedName("paid_at")
    private String paidAt;

    // JOIN fields
    @SerializedName("period_month")
    private String periodMonth;
    
    @SerializedName("invoice_total")
    private double invoiceTotal;
    
    @SerializedName("room_title")
    private String roomTitle;

    // Getters
    public String getId() { return id; }
    public String getInvoiceId() { return invoiceId; }
    public String getTenantId() { return tenantId; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getTransactionId() { return transactionId; }
    public String getStatus() { return status; }
    public String getPaidAt() { return paidAt; }

    public String getPeriodMonth() { return periodMonth; }
    public double getInvoiceTotal() { return invoiceTotal; }
    public String getRoomTitle() { return roomTitle; }
}
