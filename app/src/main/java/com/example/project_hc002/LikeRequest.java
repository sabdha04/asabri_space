package com.example.project_hc002;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LikeRequest {
    private String ktpa;

    public LikeRequest(String ktpa) {
        this.ktpa = ktpa;
    }

    public String getKtpa() {
        return ktpa;
    }
}