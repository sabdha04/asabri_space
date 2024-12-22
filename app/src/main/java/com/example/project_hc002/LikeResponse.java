package com.example.project_hc002;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LikeResponse {
    private boolean liked;
    private String message;

    public boolean isLiked() {
        return liked;
    }

    public String getMessage() {
        return message;
    }
}