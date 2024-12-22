package com.example.project_hc002;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;

public class MediaDetailActivity extends AppCompatActivity {
    private ImageView imageView;
    private VideoView videoView;
    private String mediaType;
    private String mediaPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_detail);

        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);

        // Ambil data dari Intent
        mediaType = getIntent().getStringExtra("media_type");
        mediaPath = getIntent().getStringExtra("media_path");

        if ("image".equals(mediaType)) {
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);

            // Tampilkan gambar
            Uri imageUri = Uri.parse(mediaPath);
            imageView.setImageURI(imageUri);
        } else if ("video".equals(mediaType)) {
            videoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);

            // Tampilkan video
            Uri videoUri = Uri.parse(mediaPath);
            videoView.setVideoURI(videoUri);
            videoView.start();
        } else {
            Toast.makeText(this, "Unsupported media type", Toast.LENGTH_SHORT).show();
        }
    }
}
