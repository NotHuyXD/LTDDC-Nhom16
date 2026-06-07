package com.example.rentedapp.data.model;

public class CreateAppointmentRequest {
    private String roomId;
    private String appointmentDate;
    private String appointmentTime;
    private String message;

    public CreateAppointmentRequest(String roomId, String appointmentDate, String appointmentTime, String message) {
        this.roomId = roomId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.message = message;
    }

    public String getRoomId() { return roomId; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getMessage() { return message; }
}
