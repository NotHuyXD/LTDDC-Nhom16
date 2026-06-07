package com.example.rentedapp.data.model;

public class RoomType {
    private String id;
    private String name;
    private String slug;

    public RoomType() {}

    public RoomType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }

    @Override
    public String toString() {
        return name;
    }
}
