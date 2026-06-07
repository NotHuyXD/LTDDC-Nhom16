package com.example.rentedapp.data.model;

import com.google.gson.annotations.SerializedName;

public class Conversation {
    private String id;
    @SerializedName("room_id")
    private String roomId;
    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("tenant_id")
    private String tenantId;
    @SerializedName("tenant_name")
    private String tenantName;
    @SerializedName("tenant_avatar")
    private String tenantAvatar;

    @SerializedName("landlord_id")
    private String landlordId;
    @SerializedName("landlord_name")
    private String landlordName;
    @SerializedName("landlord_avatar")
    private String landlordAvatar;

    @SerializedName("room_title")
    private String roomTitle;
    @SerializedName("last_message")
    private String lastMessage;
    @SerializedName("last_message_at")
    private String lastMessageAt;
    @SerializedName("unread_count")
    private int unreadCount;

    public String getId() { return id; }
    public String getRoomId() { return roomId; }
    public String getCreatedAt() { return createdAt; }

    public String getTenantId() { return tenantId; }
    public String getTenantName() { return tenantName; }
    public String getTenantAvatar() { return tenantAvatar; }

    public String getLandlordId() { return landlordId; }
    public String getLandlordName() { return landlordName; }
    public String getLandlordAvatar() { return landlordAvatar; }

    public String getRoomTitle() { return roomTitle; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageAt() { return lastMessageAt; }
    public int getUnreadCount() { return unreadCount; }

    public String getPartnerName(String currentUserId) {
        if (currentUserId != null && currentUserId.equals(tenantId)) {
            return landlordName;
        }
        return tenantName;
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

    public String getPartnerAvatarUrl(String currentUserId) {
        String avatar = (currentUserId != null && currentUserId.equals(tenantId)) ? landlordAvatar : tenantAvatar;
        if (avatar == null || avatar.isEmpty()) {
            return null;
        }
        if (avatar.startsWith("http")) {
            return avatar;
        }
        String origin = getBaseOrigin();
        return origin + (avatar.startsWith("/") ? "" : "/") + avatar;
    }
}
