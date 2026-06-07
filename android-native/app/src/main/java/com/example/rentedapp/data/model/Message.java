package com.example.rentedapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Message {
    private String id;
    @SerializedName("sender_id")
    private String senderId;
    private String content;
    @SerializedName("is_read")
    private int isRead;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("sender_name")
    private String senderName;
    @SerializedName("sender_avatar")
    private String senderAvatar;

    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getContent() { return content; }
    public boolean isRead() { return isRead == 1; }
    public String getCreatedAt() { return createdAt; }
    public String getSenderName() { return senderName; }
    public String getSenderAvatar() { return senderAvatar; }

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

    public String getAbsoluteSenderAvatarUrl() {
        if (senderAvatar == null || senderAvatar.isEmpty()) {
            return null;
        }
        if (senderAvatar.startsWith("http")) {
            return senderAvatar;
        }
        String origin = getBaseOrigin();
        return origin + (senderAvatar.startsWith("/") ? "" : "/") + senderAvatar;
    }
}
