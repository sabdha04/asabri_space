package com.example.project_hc002;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_hc002.R;

public class BintangKejoraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bintang_kejora); // Menghubungkan XML dengan Activity

        // Inisialisasi tombol kembali
        ImageView backButton = findViewById(R.id.back_button);

        // Event Listener untuk tombol kembali
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aksi yang dilakukan ketika tombol kembali ditekan
                Toast.makeText(BintangKejoraActivity.this, "Kembali ditekan", Toast.LENGTH_SHORT).show();
                onBackPressed(); // Menutup Activity
            }
        });
    }
}
