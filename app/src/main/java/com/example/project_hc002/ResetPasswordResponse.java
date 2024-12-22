package com.example.project_hc002;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ResetPasswordResponse {
    private boolean success;
    private String message;

    public ResetPasswordResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
