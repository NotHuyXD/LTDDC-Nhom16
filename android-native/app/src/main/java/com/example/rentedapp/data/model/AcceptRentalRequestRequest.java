package com.example.rentedapp.data.model;

public class AcceptRentalRequestRequest {
    private String startDate;
    private String endDate;
    private String terms;

    public AcceptRentalRequestRequest(String startDate, String endDate, String terms) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.terms = terms;
    }

    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public String getTerms() { return terms; }
}
