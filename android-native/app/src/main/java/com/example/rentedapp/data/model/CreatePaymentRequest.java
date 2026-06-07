package com.example.rentedapp.data.model;

public class CreatePaymentRequest {
    private String invoiceId;
    private double amount;
    private String method;

    public CreatePaymentRequest(String invoiceId, double amount, String method) {
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.method = method;
    }

    public String getInvoiceId() { return invoiceId; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
}
