package com.example.rentedapp.data.model;

public class Bookmark {
    private String id;
    private String room_id;
    private String title;
    private String price;
    private String address;
    private Double area;
    private String cover_image;

    public String getId() { return id; }
    public String getRoomId() { return room_id; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getAddress() { return address; }
    public Double getArea() { return area; }
    public String getCoverImage() { return cover_image; }

    public Room toRoom() {
        return new Room(room_id, title, price, address, "", area, cover_image, null);
    }
}
