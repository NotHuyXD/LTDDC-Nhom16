package com.example.rentedapp.data.model;

public class AddBookmarkRequest {
    private String roomId;

    public AddBookmarkRequest(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
