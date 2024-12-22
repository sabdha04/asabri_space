package com.example.project_hc002;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CommentRequest {
    private int post_id;
    private String ktpa;
    private String content;

    public CommentRequest(int post_id, String ktpa, String content) {
        this.post_id = post_id;
        this.ktpa = ktpa;
        this.content = content;
    }

    public int getPostId() {
        return post_id;
    }

    public String getKtpa() {
        return ktpa;
    }

    public String getContent() {
        return content;
    }
}