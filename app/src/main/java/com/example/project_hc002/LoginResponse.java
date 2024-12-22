package com.example.project_hc002.models;
import com.example.project_hc002.Peserta;
import com.example.project_hc002.Staff;

public class LoginResponse {
    private boolean success;
    private String message;
    private Staff staff;
    private Peserta peserta;

    // Constructor
    public LoginResponse(boolean success, String message, Staff staff) {
        this.success = success;
        this.message = message;
        this.staff = staff;
        this.peserta = peserta;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Staff getStaff() {
        return staff;
    }
    public Peserta getPeserta() {
        return peserta;
    }
    public void setStaff(Staff staff) {
        this.staff = staff;
    }
}