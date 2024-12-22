package com.example.project_hc002;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CommentResponse {
    private int id;
    private int post_id;
    private String ktpa;
    private String content;
    private String created_at;
    private String updated_at;
    private String nama;

    public int getId() { return id; }
    public int getPostId() { return post_id; }
    public String getKtpa() { return ktpa; }
    public String getContent() { return content; }
    public String getCreatedAt() { return created_at; }
    public String getUpdatedAt() { return updated_at; }
    public String getNama() { return nama; }
}