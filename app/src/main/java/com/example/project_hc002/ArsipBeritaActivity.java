package com.example.project_hc002;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ArsipBeritaActivity extends AppCompatActivity {

    // Status untuk ikon
    private boolean isLiked = false;
    private boolean isCommented = false;
    private boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arsip_berita);

        // Tombol kembali
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            Toast.makeText(this, "Kembali", Toast.LENGTH_SHORT).show();
            finish(); // Mengakhiri Activity, kembali ke ProfileActivity
        });

        // Ikon aksi
        ImageView iconLike = findViewById(R.id.icon_like);
        ImageView iconComment = findViewById(R.id.icon_comment);
        ImageView iconSave = findViewById(R.id.icon_save);
        ImageView iconShare = findViewById(R.id.icon_share);

        // Aksi untuk ikon Like
        iconLike.setOnClickListener(v -> {
            isLiked = !isLiked; // Toggle status
            iconLike.setSelected(isLiked); // Ubah tampilan berdasarkan status
            String message = isLiked ? "Anda menyukai berita ini" : "Batal menyukai berita";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        // Aksi untuk ikon Comment
        iconComment.setOnClickListener(v -> {
            isCommented = !isCommented; // Toggle status
            iconComment.setSelected(isCommented); // Ubah tampilan
            String message = isCommented ? "Anda ingin berkomentar" : "Batal komentar";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        // Aksi untuk ikon Save
        iconSave.setOnClickListener(v -> {
            isSaved = !isSaved; // Toggle status
            iconSave.setSelected(isSaved); // Ubah tampilan
            String message = isSaved ? "Berita disimpan" : "Batal menyimpan berita";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });

        // Aksi untuk ikon Share
        iconShare.setOnClickListener(v -> {
            Toast.makeText(this, "Berbagi berita", Toast.LENGTH_SHORT).show();
            // Logika tambahan untuk membagikan berita dapat ditambahkan di sini
        });
    }
}
