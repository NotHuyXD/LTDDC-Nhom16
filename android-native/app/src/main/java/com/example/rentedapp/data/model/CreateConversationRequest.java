package com.example.rentedapp.data.model;

import com.google.gson.annotations.SerializedName;

public class CreateConversationRequest {
    @SerializedName("landlordId")
    private String landlordId;
    @SerializedName("roomId")
    private String roomId;

    public CreateConversationRequest(String landlordId, String roomId) {
        this.landlordId = landlordId;
        this.roomId = roomId;
    }

    public String getLandlordId() {
        return landlordId;
    }

    public String getRoomId() {
        return roomId;
    }
}
