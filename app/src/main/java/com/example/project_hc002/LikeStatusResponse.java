package com.example.project_hc002;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LikeStatusResponse {
    private int likeCount;
    private boolean userLiked;

    public int getLikeCount() {
        return likeCount;
    }

    public boolean isUserLiked() {
        return userLiked;
    }
}