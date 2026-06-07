package com.example.rentedapp.data.model;

public class CreateInvoiceRequest {
    private String contractId;
    private String periodMonth;
    private double baseRent;
    private double electricUsage;
    private double waterUsage;
    private double electricFee;
    private double waterFee;
    private double otherFees;
    private String dueDate;

    public CreateInvoiceRequest(String contractId, String periodMonth, double baseRent,
                                double electricUsage, double waterUsage, double electricFee,
                                double waterFee, double otherFees, String dueDate) {
        this.contractId = contractId;
        this.periodMonth = periodMonth;
        this.baseRent = baseRent;
        this.electricUsage = electricUsage;
        this.waterUsage = waterUsage;
        this.electricFee = electricFee;
        this.waterFee = waterFee;
        this.otherFees = otherFees;
        this.dueDate = dueDate;
    }

    public String getContractId() { return contractId; }
    public String getPeriodMonth() { return periodMonth; }
    public double getBaseRent() { return baseRent; }
    public double getElectricUsage() { return electricUsage; }
    public double getWaterUsage() { return waterUsage; }
    public double getElectricFee() { return electricFee; }
    public double getWaterFee() { return waterFee; }
    public double getOtherFees() { return otherFees; }
    public String getDueDate() { return dueDate; }
}
