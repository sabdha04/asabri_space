package com.example.project_hc002;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;

public class koneksi extends AppCompatActivity {
    Button peserta2,buttonstaff2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_koneksi);

        peserta2 = findViewById(R.id.btnpeserta);
        buttonstaff2 = findViewById(R.id.buttonstaff);

        peserta2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putBoolean("isLoggedIn", true);
//                editor.apply();

                Intent intent = new Intent(koneksi.this, loginpeserta.class);
                startActivity(intent);

            }
        });

        buttonstaff2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke aktivitas login staff
                Intent intent = new Intent(koneksi.this, loginstaff.class);
                startActivity(intent);
            }
        });
    }
}