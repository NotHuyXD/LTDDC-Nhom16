package com.example.rentedapp.data.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Room {
    private String id;
    private String title;
    private String price;
    private String address;
    private String description;
    private Double area;
    private String cover_image;

    private String status;
    private Object images;

    @com.google.gson.annotations.SerializedName("landlord_id")
    private String landlordId;

    @com.google.gson.annotations.SerializedName("landlord_name")
    private String landlordName;

    @com.google.gson.annotations.SerializedName("landlord")
    private Landlord landlord;

    public static class Landlord {
        private String id;
        @com.google.gson.annotations.SerializedName("fullName")
        private String fullName;

        public String getId() { return id; }
        public String getFullName() { return fullName; }
    }

    public Room() {
    }

    public Room(String id, String title, String price, String address, String description, Double area, String cover_image, List<String> images) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.address = address;
        this.description = description;
        this.area = area;
        this.cover_image = cover_image;
        this.images = images;
    }

    public String getId() { return id; }
    public String getStatus() { return status; }
    public String getTitle() { return title; }
    public String getPrice() { return price; }
    public String getAddress() { return address; }
    public String getDescription() { return description; }
    public Double getArea() { return area; }
    public String getCoverImage() { return cover_image; }
    public List<String> getImages() { return getAbsoluteImageUrls(); }
    public String getLandlordId() {
        if (landlord != null) {
            return landlord.getId();
        }
        return landlordId;
    }

    public String getLandlordName() {
        if (landlord != null) {
            return landlord.getFullName();
        }
        return landlordName;
    }

    private String getBaseOrigin() {
        String baseUrl = "http://10.0.2.2:5000/api/v1/";
        try {
            baseUrl = com.example.rentedapp.data.network.ApiClient.BASE_URL;
        } catch (Exception e) {}
        if (baseUrl.contains("/api/v1/")) {
            return baseUrl.split("/api/v1/")[0];
        }
        return "http://10.0.2.2:5000";
    }

    public String getAbsoluteCoverImageUrl() {
        if (cover_image == null || cover_image.isEmpty()) {
            List<String> urls = getAbsoluteImageUrls();
            if (urls != null && !urls.isEmpty()) {
                return urls.get(0);
            }
            return null;
        }
        if (cover_image.startsWith("http")) {
            return cover_image;
        }
        String origin = getBaseOrigin();
        return origin + (cover_image.startsWith("/") ? "" : "/") + cover_image;
    }

    public List<String> getAbsoluteImageUrls() {
        List<String> urls = new ArrayList<>();
        if (images != null) {
            String origin = getBaseOrigin();
            if (images instanceof List) {
                for (Object item : (List<?>) images) {
                    if (item instanceof String) {
                        String url = (String) item;
                        if (!url.isEmpty()) {
                            if (url.startsWith("http")) {
                                urls.add(url);
                            } else {
                                urls.add(origin + (url.startsWith("/") ? "" : "/") + url);
                            }
                        }
                    } else if (item instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) item;
                        Object urlObj = map.get("url");
                        if (urlObj instanceof String) {
                            String url = (String) urlObj;
                            if (!url.isEmpty()) {
                                if (url.startsWith("http")) {
                                    urls.add(url);
                                } else {
                                    urls.add(origin + (url.startsWith("/") ? "" : "/") + url);
                                }
                            }
                        }
                    }
                }
            }
        }
        return urls;
    }
}
