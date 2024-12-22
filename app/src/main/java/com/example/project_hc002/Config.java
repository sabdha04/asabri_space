package com.example.project_hc002;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Config {
    // Change this when switching networks
    public static final String SERVER_IP = "192.168.0.7";
    public static final String BASE_URL = "http://" + SERVER_IP + "/api/";
}