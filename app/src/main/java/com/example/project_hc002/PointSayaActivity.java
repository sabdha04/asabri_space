package com.example.project_hc002;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PointSayaActivity extends AppCompatActivity {

    private TextView pointValue;
    private Button btnHistory;
    private ProgressBar progressBar;
    private ImageView btnabout, btnBack, btnbintang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_saya);

        // Inisialisasi komponen
        pointValue = findViewById(R.id.point_value);
        btnHistory = findViewById(R.id.btn_history);
        progressBar = findViewById(R.id.progressBar);
        btnabout = findViewById(R.id.btn_about);
        btnBack = findViewById(R.id.btnBack);
        btnbintang = findViewById(R.id.btnbintang);

        // Atur progress awal
        int currentPoints = 400; // Nilai poin saat ini
        int maxPoints = 500; // Nilai maksimal poin untuk level
        int progress = (currentPoints * 100) / maxPoints;
        progressBar.setProgress(progress);

        // Set TextView untuk menampilkan poin saat ini
        pointValue.setText(String.valueOf(currentPoints));

        // Listener untuk tombol Riwayat
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke RiwayatPoinActivity
                Intent intent = new Intent(PointSayaActivity.this, RiwayatPoinActivity.class);
                startActivity(intent);
            }
        });

        // Listener untuk tombol About
        btnabout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke LevelActivity
                Intent intent = new Intent(PointSayaActivity.this, LevelActivity.class);
                startActivity(intent);
            }
        });

        // Listener untuk tombol Bintang
        btnbintang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke BintangKejoraActivity
                Intent intent = new Intent(PointSayaActivity.this, BintangKejoraActivity.class);
                startActivity(intent);
            }
        });

        // Listener untuk tombol Back
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Menutup aktivitas dan kembali ke sebelumnya
            }
        });
    }
}
