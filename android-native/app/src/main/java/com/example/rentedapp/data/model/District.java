package com.example.rentedapp.data.model;

public class District {
    private String id;
    private String name;
    private String code;

    public District() {}

    public District(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }

    @Override
    public String toString() {
        return name;
    }
}
