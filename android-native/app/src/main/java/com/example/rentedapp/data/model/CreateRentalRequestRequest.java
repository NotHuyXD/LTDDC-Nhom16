package com.example.rentedapp.data.model;

public class CreateRentalRequestRequest {
    private String roomId;
    private String moveInDate;
    private int numPeople;
    private String message;

    public CreateRentalRequestRequest(String roomId, String moveInDate, int numPeople, String message) {
        this.roomId = roomId;
        this.moveInDate = moveInDate;
        this.numPeople = numPeople;
        this.message = message;
    }

    public String getRoomId() { return roomId; }
    public String getMoveInDate() { return moveInDate; }
    public int getNumPeople() { return numPeople; }
    public String getMessage() { return message; }
}
