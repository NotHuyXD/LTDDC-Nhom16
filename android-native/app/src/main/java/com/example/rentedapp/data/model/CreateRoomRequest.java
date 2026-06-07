package com.example.rentedapp.data.model;

import java.util.List;

public class CreateRoomRequest {
    private String title;
    private String description;
    private String roomTypeId;
    private String wardId;
    private String address;
    private Double area;
    private Double price;
    private Double deposit;
    private Integer maxOccupants;
    private List<String> images;

    public CreateRoomRequest(String title, String description, String roomTypeId, String wardId, String address, Double area, Double price, Double deposit, Integer maxOccupants, List<String> images) {
        this.title = title;
        this.description = description;
        this.roomTypeId = roomTypeId;
        this.wardId = wardId;
        this.address = address;
        this.area = area;
        this.price = price;
        this.deposit = deposit;
        this.maxOccupants = maxOccupants;
        this.images = images;
    }
}
